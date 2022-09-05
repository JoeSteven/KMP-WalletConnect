package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.*
import com.mimao.kmp.walletconnect.utils.UUID
import io.ktor.util.collections.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class WCClient(
    private val store: WCConnectionPersistStore,
    coroutineContext: CoroutineContext = Dispatchers.Default
) {
    private val scope = CoroutineScope(coroutineContext)
    private val _connections = MutableStateFlow<MutableMap<String, Pair<WCConnection, WCSession>>>(ConcurrentMap())
    val message: Flow<WCMessage> by lazy {
        _connections.flatMapLatest { connections ->
            merge(
                *connections.values.map {
                    it.second.message.map { method ->
                        WCMessage(
                            connectionId = it.first.id,
                            method = method
                        )
                    }
                }.toTypedArray()
            )
        }
    }
    val connections: Flow<List<WCConnection>>
        get() = _connections.map {
            it.values.map { pair ->
                pair.first
            }.toList()
        }

    fun getConnection(connectionId: String) = _connections.value[connectionId]?.first

    init {
        scope.launch {
            message.collect {
                if (it.method is WCMethod.Request) {
                    val update = it.method.params.firstOrNull()
                    if (update is WCMethod.Request.Params.Update) {
                        if (!update.approved) {
                            removeCollection(it.connectionId)
                        }
                    }
                }
            }
        }
        scope.launch {
            store.all().forEach {
                _connections.value[it.id] = Pair(it, WCSession(
                    config = it.config,
                    remotePeerId = it.peerId
                ).apply {
                    connectSocket(it.clientId)
                })
            }
        }
    }


    suspend fun connect(
        config: WCSessionConfig,
        clientMeta: WCPeerMeta,
        chainId: Long? = null
    ): Result<WCConnection> {
        val session = WCSession(
            config = config,
        )
        val requestId = createCallId()
        val clientId = UUID.randomUUID()
        session.connectSocket(clientId = clientId)
        return session.send(
            WCMethod.Request(
                id = requestId,
                type = WCMethodType.SESSION_REQUEST,
                params = listOf(
                    WCMethod.Request.Params.Request(
                        chainId = chainId,
                        peerId = clientId,
                        peerMeta = clientMeta
                    )
                )
            )
        ).let {
            runCatching {
                val result = (it as WCMethod.Response).result as WCMethod.Response.RequestResponse
                if (result.approved) {
                    WCConnection(
                        id = UUID.randomUUID(),
                        config = config,
                        clientId = clientId,
                        peerMeta = result.peerMeta,
                        accounts = result.accounts.orEmpty(),
                        chainId = result.chainId ?: -1,
                        peerId = result.peerId,
                        clientMeta = clientMeta
                    ).also {
                        storeCollection(
                            wcCollection = it,
                            session = session
                        )
                    }
                } else {
                    throw Error("Session request not approved")
                }
            }
        }
    }

    suspend fun disconnect(
        connectionId: String
    ) {
        removeCollection(connectionId)?.let {
            it.second.send(
                WCMethod.Request(
                    id = createCallId(),
                    type = WCMethodType.SESSION_UPDATE,
                    params = listOf(
                        WCMethod.Request.Params.Update(
                            approved = false,
                            chainId = it.first.chainId,
                            accounts = it.first.accounts,
                        )
                    )
                )
            )
        }
    }

    private fun createCallId() = Clock.System.now().toEpochMilliseconds()

    private suspend fun storeCollection(wcCollection: WCConnection, session: WCSession) {
        store.store(storeId = wcCollection.config.topic, connection = wcCollection)
        _connections.value[wcCollection.id] = Pair(wcCollection, session)
    }

    private suspend fun removeCollection(id: String):Pair<WCConnection, WCSession>? {
        val pair = _connections.value[id]
        pair?.first?.let {
            store.remove(storeId = it.config.topic)
            _connections.value.remove(it.id)?.second?.closeSocket()
        }
        return pair
    }
}
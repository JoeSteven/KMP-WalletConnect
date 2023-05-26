package com.mimao.kmp.walletconnect.core

import com.mimao.kmp.walletconnect.WCConnectionPersistStore
import com.mimao.kmp.walletconnect.entity.*
import io.ktor.util.collections.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
internal class WCCollectionsManager(
    private val store: WCConnectionPersistStore,
    coroutineContext: CoroutineContext = Dispatchers.Default
) {
    private val scope = CoroutineScope(coroutineContext)
    private val _connectionMap = ConcurrentMap<String, Pair<WCConnection, WCSession>>()
    private val _connections = MutableSharedFlow<MutableMap<String, Pair<WCConnection, WCSession>>>(replay = 1)
        .apply { tryEmit(_connectionMap) }

    private suspend fun MutableSharedFlow<MutableMap<String, Pair<WCConnection, WCSession>>>.update(block: suspend (MutableMap<String, Pair<WCConnection, WCSession>>) -> Unit) {
        block(_connectionMap)
        emit(_connectionMap)
    }

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

    val connections = _connections.map {
        it.values.map { pair ->
            pair.first
        }.toList()
    }
    private val initialize = MutableSharedFlow<Boolean>(replay = 1)
        .apply {
            tryEmit(false)
        }

    fun getConnection(connectionId: String) = initialize.filter { it }.flatMapLatest {
        _connections
    }.map {
        it[connectionId]?.first
    }

    fun getSession(connectionId: String) = _connectionMap[connectionId]?.second

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
                _connections.update { map ->
                    map[it.id] = Pair(it, WCSession(
                        config = it.config,
                        remotePeerId = it.peerId,
                        clientId = it.clientId
                    ).apply {
                        try {
                            connectSocket()
                            send(
                                method = WCMethod.Request(
                                    id = createCallId(),
                                    type = WCMethodType.SESSION_UPDATE,
                                    params = listOf(
                                        WCMethod.Request.Params.Update(
                                            approved = true,
                                            chainId = it.chainId,
                                            accounts = it.accounts
                                        )
                                    )
                                ),
                                ignoreResponse = true
                            )
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    })
                }

            }
            initialize.emit(true)
        }
    }

    suspend fun disconnect(
        connectionId: String
    ) {
        removeCollection(connectionId, beforeRemove = {
            it.second.send(
                method = WCMethod.Request(
                    id = createCallId(),
                    type = WCMethodType.SESSION_UPDATE,
                    params = listOf(
                        WCMethod.Request.Params.Update(
                            approved = false,
                            chainId = it.first.chainId,
                            accounts = it.first.accounts,
                        )
                    )
                ),
                ignoreResponse = true
            )
        })
    }

    fun createCallId() = Clock.System.now().toEpochMilliseconds()

    suspend fun storeCollection(wcCollection: WCConnection, session: WCSession) {
        store.store(storeId = wcCollection.config.topic, connection = wcCollection)
        _connections.update {
            _connectionMap[wcCollection.id] = Pair(wcCollection, session)
        }
    }

    suspend fun removeCollection(
        id: String,
        beforeRemove: suspend (Pair<WCConnection, WCSession>) -> Unit = {}
    ): Pair<WCConnection, WCSession>? {
        val pair = _connectionMap[id]
        pair?.let {
            beforeRemove.invoke(it)
        }
        pair?.first?.let {
            store.remove(storeId = it.config.topic)
            _connections.update { map ->
                map.remove(it.id)?.second?.closeSocket()
            }
        }
        return pair
    }
}
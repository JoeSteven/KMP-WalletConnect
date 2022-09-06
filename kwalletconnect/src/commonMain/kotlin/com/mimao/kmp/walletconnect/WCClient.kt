package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.core.WCCollectionsManager
import com.mimao.kmp.walletconnect.core.WCSession
import com.mimao.kmp.walletconnect.entity.*
import com.mimao.kmp.walletconnect.utils.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.JsonArray
import kotlin.coroutines.CoroutineContext

class WCClient(
    store: WCConnectionPersistStore,
    coroutineContext: CoroutineContext = Dispatchers.Default
) {

    private val manager by lazy {
        WCCollectionsManager(
            store = store,
            coroutineContext = coroutineContext
        )
    }

    val connections = manager.connections

    fun getConnection(connectionId: String) = manager.getConnection(connectionId)

    suspend fun connect(
        config: WCSessionConfig,
        clientMeta: WCPeerMeta,
        chainId: Long? = null
    ): Result<WCConnection> {
        return runCatching {
            val requestId = manager.createCallId()
            val clientId = UUID.randomUUID()
            val session = WCSession(
                config = config,
                clientId = clientId
            )
            session.connectSocket()
            session.send(
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
                if (it is WCMethod.Error) {
                    throw Error("code:${it.code} message:${it.error}")
                }
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
                        manager.storeCollection(
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

    suspend fun disconnect(connectionId: String) = manager.disconnect(connectionId)

    suspend fun request(
        connectionId: String,
        method: String,
        params: JsonArray
    ): Result<WCMethod> {
        return runCatching {
            manager.getSession(connectionId)?.send(
                method = WCMethod.CustomRequest(
                    id = manager.createCallId(),
                    method = method,
                    params = params
                )
            ) ?: throw Error("Can't find any connections with id:$connectionId")
        }
    }

}
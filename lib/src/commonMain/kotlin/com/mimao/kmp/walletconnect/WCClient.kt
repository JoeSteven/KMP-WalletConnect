package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.WCConnection
import com.mimao.kmp.walletconnect.entity.WCMethod
import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import io.ktor.util.collections.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class WCClient {
    private val _connections = MutableStateFlow<MutableMap<String, Pair<WCConnection, WCSession>>>(ConcurrentMap())

    fun getConnectionList():Flow<List<WCConnection>> {
        return _connections.map {
            it.values.map { pair ->
                pair.first
            }.toList()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun message():Flow<WCMethod> {
        return _connections.flatMapLatest {
            merge(*it.values.map { pair ->
                pair.second.message
            }.toTypedArray())
        }
    }

    suspend fun connect(
        config: WCSessionConfig,
        peerMeta: WCPeerMeta,
    ): Result<WCConnection> {
        val session = WCSession(config, peerMeta)
        val result = WCSession(
            config = config,
            peerMeta = peerMeta
        ).message
            .filterIsInstance<WCMethod.Response>()
            .filter {
                it.id == 0L
            }.map {
                it.result
            }.filterIsInstance<WCMethod.Response.RequestResponse>()
            .map {
                if (!it.approved || it.peerMeta == null) {
                    Result.failure(Error("Connection rejected"))
                } else {
                    Result.success(
                        WCConnection(
                            id = "123",
                            config = config,
                            peerMeta = it.peerMeta,
                            accounts = it.accounts.orEmpty(),
                            chainId = it.chainId ?: -1
                        ).also {

                        }
                    )
                }
            }.single()
        session.connectSocket()
        return result

    }

//    fun sign(
//        connection: WCConnection,
//        message: String,
//    ): Flow<Result<String>> {
//
//    }
}
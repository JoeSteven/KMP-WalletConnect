package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.WCMessage
import com.mimao.kmp.walletconnect.entity.WCMethod
import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import com.mimao.kmp.walletconnect.utils.JSON
import com.mimao.kmp.walletconnect.websocket.KtorSocket
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal class WCSession(
    private val config: WCSessionConfig,
    private val meta: WCPeerMeta,
) {
    private val socket:KtorSocket by lazy {
        KtorSocket(serverUrl = config.bridge)
    }

//    val message: Flow<WCMethod> by lazy {
//        socket.recieve.mapNotNull {
//            handleMessage(JSON.decodeFromString(WCMessage.serializer(), it))
//        }
//    }
//
//
//    suspend fun connect() {
//        socket.connect()
//    }
//
//    private suspend fun handleMessage(wcMessage: WCMessage):WCMethod? {
//        if (wcMessage.type == WCMessage.Type.Pub) return null
//        return decrypt(wcMessage.payload)
//    }
//
//    private fun decrypt(payload: String): WCMethod {
////        val encryptedPayload = JSON.decodeFromString(EncryptedPayload.serializer(), payload)
//    }


}
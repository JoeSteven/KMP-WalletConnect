package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.*
import com.mimao.kmp.walletconnect.entity.WCMessage
import com.mimao.kmp.walletconnect.entity.WCMethod
import com.mimao.kmp.walletconnect.utils.JSON
import com.mimao.kmp.walletconnect.utils.WCCipher
import com.mimao.kmp.walletconnect.utils.decodeJson
import com.mimao.kmp.walletconnect.utils.encodeJson
import com.mimao.kmp.walletconnect.websocket.KtorSocket
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*

internal class WCSession(
    val config: WCSessionConfig,
    val peerMeta: WCPeerMeta,
) {
    private var remotePeerId: String? = null
    private val key:String by lazy {
        config.key
    }

    private val socket:KtorSocket by lazy {
        KtorSocket(serverUrl = config.bridge)
    }

    val message: Flow<WCMethod> by lazy {
        socket.recieve.mapNotNull {
            handleMessage(JSON.decodeFromString(WCMessage.serializer(), it))
        }
    }

    suspend fun connectSocket() {
        socket.connect()
    }

    suspend fun closeSocket() {
        socket.close()
    }

    suspend fun send(
        method: WCMethod
    ):WCMethod {
        val payload = when(method) {
            is WCMethod.CustomRequest -> {
                JsonRpcRequest(
                    id = method.requestId,
                    method = method.method,
                    params = method.params
                ).encodeJson()
            }
            is WCMethod.Request -> {
                JsonRpcRequest(
                    id = method.requestId,
                    method = method.type.value,
                    params = method.params
                ).encodeJson()
            }
            is WCMethod.Response -> {
                JsonRpcResponse(
                    id = method.requestId,
                    result = method.result
                ).encodeJson()
            }

            is WCMethod.Error -> return method
        }
        val encryptedPayload = WCCipher.encrypt(key, payload)
        val result = message.filter {
            it.requestId == method.requestId
        }
        socket.send(WCMessage(
            topic = remotePeerId ?: config.topic,
            type = WCMessage.Type.Pub,
            payload = encryptedPayload.encodeJson()
        ).encodeJson())
        return result.single()
    }

    private fun handleMessage(wcMessage: WCMessage):WCMethod? {
        if (wcMessage.type == WCMessage.Type.Pub) return null
        return decrypt(wcMessage.payload)
    }


    private fun decrypt(payload: String): WCMethod? {
        var requestId:Long? =  null
        return try {
            val decrypted = WCCipher.decrypt(payload = payload, key = key)
                .decodeJson<JsonElement>()
            val result = decrypted.jsonObject["result"]
            requestId = decrypted.jsonObject["id"]?.jsonPrimitive?.content?.toLong() ?: return null
            if (result != null)  {
                WCMethod.Response(
                    id = requestId,
                    result = result
                )
            } else {
                val request = JSON.decodeFromJsonElement<JsonRpcRequest<JsonArray>>(decrypted)
                when(request.method) {
                    WCMethodType.SESSION_REQUEST.value -> {
                        WCMethod.Request(
                            id = request.id,
                            type = WCMethodType.SESSION_REQUEST,
                            params = request.params.encodeJson().decodeJson<List<WCMethod.Request.Params.Request>>()
                                .also {
                                    remotePeerId = it.first().peerId
                                }
                        )
                    }
                    WCMethodType.SESSION_UPDATE.value -> {
                        WCMethod.Request(
                            id = request.id,
                            type = WCMethodType.SESSION_UPDATE,
                            params = request.params.encodeJson().decodeJson<List<WCMethod.Request.Params.Update>>()
                        )
                    }
                    else -> {
                        WCMethod.CustomRequest(
                            id = request.id,
                            method = request.method.orEmpty(),
                            params = request.params
                        )
                    }
                }
            }

        } catch (e: Throwable) {
            WCMethod.Error(
                id = requestId ?: return null,
                error = e.toString()
            )
        }
    }
}
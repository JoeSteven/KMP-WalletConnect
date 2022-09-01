package com.mimao.kmp.walletconnect.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class KtorSocket(
    private val serverUrl: String,
    private val client: HttpClient = httpClient(),
) {
    val recieve = MutableSharedFlow<String>(1)
    val connected = MutableStateFlow(false)
    private val sendFlow = MutableSharedFlow<String>(replay = 1)
    private var sendRoutine:Job? = null
    private var rvRoutine:Job? = null

    suspend fun connect() {
        withContext(Dispatchers.Default) {
            client.webSocket(
                serverUrl.replace("https://", "wss://").replace("http://", "ws://"),
            ) {
                println("connected")
                connected.value = true
                rvRoutine = launch { receiveMessage() }
                sendRoutine = launch { sendMessage() }
                sendRoutine?.join()
                rvRoutine?.cancelAndJoin()
            }
        }

    }

    private suspend fun DefaultClientWebSocketSession.receiveMessage() {
        for (frame in incoming) {
            println("frame: $frame")
            when (frame.frameType) {
                FrameType.CLOSE -> connected.value = false
                FrameType.PING -> send(Frame.Pong(data = byteArrayOf(1)))
                else -> {
                    if (frame is Frame.Text) {
                        recieve.tryEmit(frame.readText())
                    }
                }
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage() {
        println("collect send msg")
        sendFlow.collect {
            println("send: $it")
            send(it)
        }
    }


    fun close() {
        println("close socket")
        connected.value = false
        rvRoutine?.cancel()
    }

    fun send(message: String) {
        sendFlow.tryEmit(message).also {
            println("try send: $message $it")
        }
    }
}
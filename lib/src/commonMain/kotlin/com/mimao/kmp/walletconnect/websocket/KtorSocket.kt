package com.mimao.kmp.walletconnect.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

internal class KtorSocket(
    private val serverUrl: String,
    private val client: HttpClient = httpClient(),
) {
    val recieve = MutableSharedFlow<String>()
    val connected = MutableStateFlow(false)
    private val sendFlow = MutableSharedFlow<String>(replay = 1)
    private var sendRoutine:Job? = null
    private var rvRoutine:Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    suspend fun connect() {
        scope.launch {
            client.webSocket(
                serverUrl.replace("https://", "wss://").replace("http://", "ws://"),
            ) {
                rvRoutine = launch { receiveMessage() }
                sendRoutine = launch { sendMessage() }
                connected.value = true
                println("connected")
                sendRoutine?.join()
                rvRoutine?.cancelAndJoin()
            }
        }
        while (!connected.value){
            delay(100)
        }
    }

    private suspend fun DefaultClientWebSocketSession.receiveMessage() {
        for (frame in incoming) {
            when (frame.frameType) {
                FrameType.CLOSE -> connected.value = false
                FrameType.PING -> send(Frame.Pong(data = byteArrayOf(1)))
                else -> {
                    if (frame is Frame.Text) {
                        recieve.emit(frame.readText()).also {
                            println("frame: $frame emit:$it")
                        }
                    }
                }
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage() {
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
package com.mimao.kmp.walletconnect.websocket

import com.mimao.kmp.walletconnect.utils.WCLogger
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
    internal val receive = MutableSharedFlow<String>()
    internal val connected = MutableStateFlow(false)
    private val sendFlow = MutableSharedFlow<String>(replay = 1)
    private var sendRoutine: Job? = null
    private var rvRoutine: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    suspend fun connect() {
        scope.launch {
            client.webSocket(
                serverUrl.replace("https://", "wss://").replace("http://", "ws://"),
            ) {
                rvRoutine = launch { receiveMessage() }
                sendRoutine = launch { sendMessage() }
                connected.value = true
                WCLogger.log("websocket connected:$serverUrl")
                sendRoutine?.join()
                rvRoutine?.cancelAndJoin()
            }
        }
        while (!connected.value) {
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
                        receive.emit(frame.readText())
                    }
                }
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage() {
        sendFlow.collect {
            send(it)
        }
    }


    fun close() {
        connected.value = false
        rvRoutine?.cancel()
        WCLogger.log("websocket closed:$serverUrl")
    }

    fun send(message: String) {
        sendFlow.tryEmit(message)
    }
}
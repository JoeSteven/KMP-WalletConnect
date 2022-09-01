package com.mimao.kmp.walletconnect.websocket

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import kotlin.time.Duration.Companion.seconds

fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}) = HttpClient(
    engine = provideHttpClientEngine(),
) {
    install(WebSockets)
    install(HttpTimeout){
        connectTimeoutMillis = 30.seconds.inWholeMilliseconds
        requestTimeoutMillis = 30.seconds.inWholeMilliseconds
        socketTimeoutMillis = 30.seconds.inWholeMilliseconds
    }
    config.invoke(this)
}
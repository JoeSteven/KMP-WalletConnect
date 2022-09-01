package com.mimao.kmp.walletconnect.websocket

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.*

internal actual fun provideHttpClientEngine(): HttpClientEngine {
    return Darwin.create()
}
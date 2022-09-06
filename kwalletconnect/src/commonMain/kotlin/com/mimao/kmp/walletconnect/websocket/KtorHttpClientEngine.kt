package com.mimao.kmp.walletconnect.websocket

import io.ktor.client.engine.HttpClientEngine

internal expect fun provideHttpClientEngine(): HttpClientEngine
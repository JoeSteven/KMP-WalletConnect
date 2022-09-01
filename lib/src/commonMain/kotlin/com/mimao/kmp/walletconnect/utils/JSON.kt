package com.mimao.kmp.walletconnect.utils

import kotlinx.serialization.json.Json

internal val JSON = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}
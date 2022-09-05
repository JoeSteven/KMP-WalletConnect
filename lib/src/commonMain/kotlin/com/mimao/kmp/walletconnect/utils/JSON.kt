package com.mimao.kmp.walletconnect.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

internal val JSON = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

internal inline fun <reified T> T.encodeJson(): String = JSON.encodeToString(this)

internal inline fun <reified T> String.decodeJson(): T = JSON.decodeFromString(this)
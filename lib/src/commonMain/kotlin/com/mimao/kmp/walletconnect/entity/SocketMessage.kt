package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class SocketMessage(
    val topic: String,
    val type: String,
    val payload: String
)

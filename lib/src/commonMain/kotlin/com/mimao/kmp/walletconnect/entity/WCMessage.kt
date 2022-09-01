package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WCMessage(
    val topic: String,
    val type: Type,
    val payload: String
) {
    @Serializable
    enum class Type{
        @SerialName("pub")
        Pub,
        @SerialName("sub")
        Sub
    }
}

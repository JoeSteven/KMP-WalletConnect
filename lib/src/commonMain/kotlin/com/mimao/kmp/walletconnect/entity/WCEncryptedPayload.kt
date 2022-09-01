package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WCEncryptedPayload(
    @SerialName("data") val data: String,
    @SerialName( "iv") val iv: String,
    @SerialName("hmac") val hmac: String
)
package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WCPeerMeta(
    @SerialName("description")
    val description: String? = null,
    @SerialName("icons")
    val icons: List<String>? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("url")
    val url: String? = null
)

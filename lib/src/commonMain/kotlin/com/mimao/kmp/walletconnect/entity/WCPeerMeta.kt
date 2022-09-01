package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.Serializable

@Serializable
data class WCPeerMeta(
    val name: String,
    val url: String,
    val description: String? = null,
    val icons: List<String> = listOf("")
)

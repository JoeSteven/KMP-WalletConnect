package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.Serializable

@Serializable
data class WCConnection(
    val id: String,
    val peerId: String,
    val config: WCSessionConfig,
    val clientId: String,
    val clientMeta: WCPeerMeta,
    val peerMeta: WCPeerMeta?,
    val accounts: List<String>,
    val chainId: Long,
)
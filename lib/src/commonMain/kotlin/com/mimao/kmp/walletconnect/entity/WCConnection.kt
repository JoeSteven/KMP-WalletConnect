package com.mimao.kmp.walletconnect.entity

data class WCConnection(
    val id: String,
    val config: WCSessionConfig,
    val peerMeta: WCPeerMeta,
    val accounts: List<String>,
    val chainId: Long,
)
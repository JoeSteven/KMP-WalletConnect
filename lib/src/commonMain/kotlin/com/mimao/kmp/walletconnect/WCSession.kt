package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import io.ktor.http.*

data class WCSession(
    val config: WCSessionConfig,
    val meta: WCPeerMeta,
) {


}
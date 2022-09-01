package com.mimao.kmp.walletconnect.entity

import io.ktor.http.*

data class WCSessionConfig(
    val topic: String,
    val bridge: String,
    val key: String,
    val protocol: String = "wc",
    val version: Int = 1
) {
    val uri = "wc:$topic@$version?bridge=${Url(bridge)}&key=$key"

    companion object {
        fun fromUri(uri: String):WCSessionConfig? {
            if (!uri.startsWith("wc:")) return null

            val uriString = uri.replace("wc:", "wc://")
            val uri = Url(uriString)
            val bridge = uri.parameters["bridge"]
            val key = uri.parameters["key"]
            val topic = uri.user
            val version = uri.host.toInt()

            if (bridge == null || key == null || topic == null) {
                return null
            }
            return WCSessionConfig(
                topic = topic,
                bridge = bridge,
                key = key,
                protocol = "wc",
                version = version
            )
        }
    }
}

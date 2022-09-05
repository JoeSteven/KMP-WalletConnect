package com.mimao.kmp.walletconnect.entity

import com.mimao.kmp.walletconnect.utils.UUID
import com.mimao.kmp.walletconnect.utils.hex
import com.mimao.kmp.walletconnect.utils.toUrlEncode
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class WCSessionConfig(
    val bridge: String,
    val topic: String = UUID.randomUUID(),
    val key: String = ByteArray(32).also { Random.nextBytes(it) }.hex(),
    val protocol: String = "wc",
    val version: Int = 1
) {
    val uri = "wc:$topic@$version?bridge=${bridge.toUrlEncode()}&key=$key"

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

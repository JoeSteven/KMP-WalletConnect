package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.Serializable

@Serializable
internal sealed class WCMethod(val methodId: Long) {
    @Serializable
    data class Request(val id: Long, val peerMeta: WCPeerMeta, val chainId: String?) : WCMethod(id)

    @Serializable
    data class Update(val id: Long, val params:Params):WCMethod(id) {
        @Serializable
        data class Params(
            val approved: Boolean,
            val chainId: String?,
            val accounts: List<String>?,
            val peerData: WCPeerMeta?
        )
    }

    @Serializable
    data class Transaction(
        val id: Long,
        val from: String,
        val to: String?,
        val nonce: String?,
        val gasPrice: String?,
        val gasLimit: String?,
        val value: String,
        val data: String
    ): WCMethod(id)

    @Serializable
    data class Sign(
        val id: Long,
        val address: String,
        val message: String
    )
}

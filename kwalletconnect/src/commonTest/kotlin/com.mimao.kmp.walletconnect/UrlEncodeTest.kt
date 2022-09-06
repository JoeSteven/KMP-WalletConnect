package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.utils.toUrlEncode
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlEncodeTest {
    @Test
    fun encode() {
        val url ="https://safe-walletconnect.gnosis.io"
        assertEquals(
            "https%3A%2F%2Fsafe-walletconnect.gnosis.io",
            url.toUrlEncode()
        )
    }
}
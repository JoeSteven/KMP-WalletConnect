package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.utils.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class UUIDTest {
    @Test
    fun uuid() {
        val uuid = buildList {
            for (i in 0 until 10000) {
                add(UUID.randomUUID())
            }
        }

        assertEquals(10000, uuid.size)
        assertEquals(10000, uuid.distinct().size)
    }
}
package com.mimao.kmp.walletconnect.utils

import java.util.UUID

internal actual object UUID {
    actual fun randomUUID(): String {
        return UUID.randomUUID().toString()
    }
}
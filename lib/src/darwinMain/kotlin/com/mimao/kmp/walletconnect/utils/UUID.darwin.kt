package com.mimao.kmp.walletconnect.utils

import platform.Foundation.NSUUID

internal actual object UUID {
    actual fun randomUUID(): String {
        return NSUUID.UUID().UUIDString
    }
}
package com.mimao.kmp.walletconnect.utils

import com.mimao.kmp.walletconnect.entity.WCEncryptedPayload

internal actual object WCCipher {
    actual fun decrypt(payload: String, key: String): String{
        throw NotImplementedError()
    }

    actual fun encrypt(payload: String, key: String): WCEncryptedPayload {
        throw NotImplementedError()
    }


}
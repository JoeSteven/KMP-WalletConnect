package com.mimao.kmp.walletconnect.utils

import com.mimao.kmp.walletconnect.entity.WCEncryptedPayload


internal expect object WCCipher {

    fun decrypt(payload: String, key: String): String


    fun encrypt(payload: String, key: String): WCEncryptedPayload
}
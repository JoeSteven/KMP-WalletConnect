package com.mimao.kmp.walletconnect.utils

import platform.Foundation.*


@Suppress("CAST_NEVER_SUCCEEDS")
internal actual fun String.toUrlEncode(): String{
    return (this as NSString).stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLHostAllowedCharacterSet,
    ) ?: this
}
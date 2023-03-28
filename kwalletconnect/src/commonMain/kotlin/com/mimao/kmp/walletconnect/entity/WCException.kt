package com.mimao.kmp.walletconnect.entity

class WCException(val error: WCMethod.Error): Throwable(error.toString())
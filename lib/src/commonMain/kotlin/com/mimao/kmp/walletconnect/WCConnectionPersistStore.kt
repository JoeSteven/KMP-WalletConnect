package com.mimao.kmp.walletconnect

import com.mimao.kmp.walletconnect.entity.WCConnection

interface WCConnectionPersistStore {
    suspend fun store(storeId: String,connection: WCConnection)
    suspend fun load(storeId: String): WCConnection?
    suspend fun all():List<WCConnection>
    suspend fun remove(storeId: String)
}
package me.mimao.common

import com.mimao.kmp.walletconnect.WCConnectionPersistStore
import com.mimao.kmp.walletconnect.entity.WCConnection
import io.ktor.util.collections.*

class FakeWCConnectionStore : WCConnectionPersistStore {
    private val store = ConcurrentMap<String, WCConnection>()
    override suspend fun store(storeId: String, connection: WCConnection) {
        store[storeId] = connection
    }

    override suspend fun load(storeId: String): WCConnection? {
        return store[storeId]
    }

    override suspend fun all(): List<WCConnection> {
        return store.values.toList()
    }

    override suspend fun remove(storeId: String) {
        store.remove(storeId)
    }
}
package me.mimao.common

import com.mimao.kmp.walletconnect.WCConnectionPersistStore
import com.mimao.kmp.walletconnect.entity.WCConnection
import io.ktor.util.collections.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FakeWCConnectionStore : WCConnectionPersistStore {
    private val store = ConcurrentMap<String, WCConnection>()
    override suspend fun store(storeId: String, connection: WCConnection) {
        store[storeId] = connection
    }

    override suspend fun load(storeId: String): WCConnection? {
        return store[storeId]
    }

    override suspend fun all(): List<WCConnection> {
        return store.values.toList() + listOf(
            Json.decodeFromString("""{"id":"46cabf20-224f-45bc-92a3-eed13f3d4e41","peerId":"b4e2d4b7-1a2e-4c22-98ef-03034694be8b","config":{"bridge":"https://o.bridge.walletconnect.org","topic":"475fb9d2-465e-422f-ac1c-0a6e71526959","key":"775900D778A8E819807B9EB07BCA1F444206DB79E693F464676B0D725F8A4FDE"},"clientId":"6d548b01-0316-4ee3-b833-594854c96d76","clientMeta":{"description":"a walletconnect client for kotlin multiplatform","icons":["https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXXHZPK4DZ_rjlDqj5l3SwjWZ5zD3bmte9pQ&usqp=CAU"],"name":"Kmp client","url":"https://github.com/JoeSteven/KMP-WalletConnect"},"peerMeta":{"description":"MetaMask Mobile app","icons":["https://raw.githubusercontent.com/MetaMask/brand-resources/master/SVG/metamask-fox.svg"],"name":"MetaMask","url":"https://metamask.io"},"accounts":["0x2c2201da12a2fe5e4f7ea7d1a696e08981f68171"],"chainId":1}
"""),
        )
    }

    override suspend fun remove(storeId: String) {
        store.remove(storeId)
    }
}
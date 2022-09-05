package me.mimao.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.mimao.kmp.walletconnect.WCClient
import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import com.mimao.kmp.walletconnect.utils.WCLogger
import kotlinx.coroutines.launch

@Composable
fun App() {
    WCLogger.switch(true)
    val wcClient = remember {
        WCClient(
            store = FakeWCConnectionStore(),
        )
    }
    val scope = rememberCoroutineScope()
    val connections by wcClient.connections.collectAsState(emptyList())
    var uri by remember {
        mutableStateOf("")
    }
    Column {
        Row {
            Button(onClick = {
                scope.launch {
                    wcClient.connect(
                        config = WCSessionConfig(
                            bridge = "https://safe-walletconnect.gnosis.io",
                        ).also {
                            uri = it.uri
                        },
                        clientMeta = WCPeerMeta(
                            name = "Kmp client",
                            description = "a walletconnect client for kotlin multiplatform",
                            icons = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXXHZPK4DZ_rjlDqj5l3SwjWZ5zD3bmte9pQ&usqp=CAU"),
                            url = "https://github.com/JoeSteven/KMP-WalletConnect"
                        )
                    ).onFailure {
                        uri = it.toString()
                    }
                }
            }) {
                Text("Connect new session")
            }
            if (connections.isNotEmpty()) {
                Button(onClick = {
                    scope.launch {
                        wcClient.disconnect(
                            connections.last().id
                        )
                    }
                }) {
                    Text("Disconnect")
                }
            }
        }
        Text(uri)
        println("$uri")
        LazyColumn {
            items(connections) {
                Text("connected:$it")
            }
        }
    }

}

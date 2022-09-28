package me.mimao.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.mimao.kmp.walletconnect.WCClient
import com.mimao.kmp.walletconnect.entity.WCMethod
import com.mimao.kmp.walletconnect.entity.WCMethodType
import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import com.mimao.kmp.walletconnect.utils.WCLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray

@OptIn(ExperimentalMaterialApi::class)
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
    var connectJob: Job? = remember {
        null
    }
    Column {
        Row {
            Button(onClick = {
                connectJob = scope.launch {
                    wcClient.connect(
                        config = WCSessionConfig(
                            bridge = "https://bridge.walletconnect.org",
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
                        println("onFailure:$it")
                        uri = it.toString()
                    }
                }
            }) {
                Text("Connect new session")
            }
        }
        ListItem(
            trailing = {
                Button(onClick = {
                    connectJob?.cancel()
                    uri = ""
                }) {
                    Text("Cancel")
                }
            }
        ) {
            Text(uri)
        }

        println(uri)
        LazyColumn {
            items(connections) {
                ListItem(
                    trailing = {
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    wcClient.request(
                                        connectionId = it.id,
                                        method = "personal_sign",
                                        params = Json.encodeToJsonElement(
                                            listOf(
                                                "0x48656c6c6f2c207765623321",
                                                it.accounts.first(),
                                            )
                                        ).jsonArray
                                    ).onSuccess {
                                        println("Sign success:$it")
                                    }
                                }
                            }) {
                                Text("Sign")
                            }
                            Button(onClick = {
                                scope.launch {
                                    wcClient.disconnect(it.id)
                                }
                            }) {
                                Text("Disconnect")
                            }
                        }

                    }
                ) {
                    Text("${it.peerMeta?.name}:${it.peerId}")
                }

                println(Json.encodeToString(it))
            }
        }

    }

}

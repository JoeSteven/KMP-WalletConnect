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
import com.mimao.kmp.walletconnect.entity.WCPeerMeta
import com.mimao.kmp.walletconnect.entity.WCSessionConfig
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClientContent(
    newUri: (String) -> Unit
) {
    val wcClient = remember {
        WCClient(
            store = FakeWCConnectionStore(),
        )
    }
    val scope = rememberCoroutineScope()
    val connections by wcClient.connections.collectAsState(emptyList())

    var signResponse by remember {
        mutableStateOf<String?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    Column {
        Text("WalletConnect Client:")
        Row {
            Button(onClick = {
                scope.launch {
                    wcClient.connect(
                        config = WCSessionConfig(
                            bridge = "https://6.bridge.walletconnect.org",
                        ).also {
                            newUri(it.uri)
                        },
                        clientMeta = WCPeerMeta(
                            name = "Kmp client",
                            description = "a walletconnect client for kotlin multiplatform",
                            icons = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXXHZPK4DZ_rjlDqj5l3SwjWZ5zD3bmte9pQ&usqp=CAU"),
                            url = "https://github.com/JoeSteven/KMP-WalletConnect"
                        )
                    ).onFailure {
                        newUri(it.toString())
                    }
                }
            }) {
                Text("Client new connection")
            }
        }

        signResponse?.let {
            Text("Response:$it")
        }

        error?.let {
            Text("Response failed:$it")
        }

        LazyColumn {
            items(connections) {
                ListItem(
                    trailing = {
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    error = null
                                    signResponse = null
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
                                        if (it is WCMethod.Response) {
                                            signResponse = it.result.toString()
                                        } else if (it is WCMethod.Error) {
                                            error = it.toString()
                                        }
                                    }.onFailure {
                                        error = it.toString()
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

                    },
                    secondaryText = {
                        Text(text ="Self id:${it.clientId}")
                    }
                ) {
                    Text("${it.peerMeta?.name}:${it.peerId}")
                }
            }
        }

    }
}
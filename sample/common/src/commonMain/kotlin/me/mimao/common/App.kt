package me.mimao.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimao.kmp.walletconnect.WCClient
import com.mimao.kmp.walletconnect.WCServer
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

@Composable
fun App() {
    WCLogger.switch(true)
    var uri by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ClientContent(uri = uri, newUri = {
            uri = it
        })
        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        ServerContent(uri = uri)
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ColumnScope.ServerContent(uri: String) {
    val scope = rememberCoroutineScope()
    val wcServer = remember {
        WCServer(
            store = FakeWCConnectionStore(),
        )
    }
    var pairRequest by remember {
        mutableStateOf<WCServer.PairRequest?>(null)
    }
    var error by remember {
        mutableStateOf<String?>(null)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    Column {
        Text("WalletConnect Server:")
        Row {
            Button(onClick = {
                error = null
                loading = true
                scope.launch {
                    wcServer.pair(
                        uri = uri,
                        clientMeta = WCPeerMeta(
                            name = "Kmp Server",
                        )
                    ).onSuccess {
                        loading = false
                        pairRequest = it
                    }.onFailure {
                        loading = false
                        error = it.toString()
                    }
                }
            }) {
                Text("Pair")
            }
        }
        pairRequest?.let {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                trailing = {
                    Row {
                        Button(
                            onClick = {
                                scope.launch {
                                    it.approve(
                                        account = listOf("0xD97bBF1e644E9C33A4d7C0e2E54A5B00738e3C36"),
                                        chainId = 1
                                    ).onSuccess {
                                        pairRequest = null
                                    }.onFailure {
                                        error = it.toString()
                                    }
                                }
                            }
                        ) {
                            Text("Approve")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    it.reject()
                                    pairRequest = null
                                }
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                },
                secondaryText = {
                    Text(text = it.peerId)
                }
            ) {
                Text(
                    text = "${it.peerMeta?.name}",
                )
            }
        }

        error?.let {
            Text(it)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ClientContent(
    uri: String,
    newUri: (String) -> Unit
) {
    val wcClient = remember {
        WCClient(
            store = FakeWCConnectionStore(),
        )
    }
    val scope = rememberCoroutineScope()
    val connections by wcClient.connections.collectAsState(emptyList())

    var connectJob: Job? = remember {
        null
    }
    Column {
        Text("WalletConnect Client:")
        Row {
            Button(onClick = {
                connectJob = scope.launch {
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
        ListItem(
            trailing = {
                if (uri.isNotEmpty()) {
                    Button(onClick = {
                        connectJob?.cancel()
                        newUri("it")
                    }) {
                        Text("Cancel")
                    }
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
package me.mimao.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.mimao.kmp.walletconnect.websocket.KtorSocket
import kotlinx.coroutines.launch

@Composable
fun App() {
    val socket = remember {
        KtorSocket(serverUrl = "wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self")
    }
    val scope = rememberCoroutineScope()
    val response by socket.recieve.collectAsState("text")
    val connected by socket.connected.collectAsState(false)
    Column {
        Button(onClick = {
            if (connected){
                socket.close()
            } else  {
                scope.launch {
                    socket.connect()
                }
            }

        }) {
            Text(if (connected) "Disconnect" else "Connect")
        }
        Button(onClick = {
            socket.send("Tested Miao!")
        }) {
            Text("send")
        }
        Text(response)
    }

}

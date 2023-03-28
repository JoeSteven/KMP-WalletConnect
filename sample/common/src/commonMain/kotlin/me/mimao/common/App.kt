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
import com.mimao.kmp.walletconnect.entity.*
import com.mimao.kmp.walletconnect.utils.WCLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(uri)
        Spacer(modifier = Modifier.height(8.dp))
        println(uri)
        ClientContent(
            newUri = {
                uri = it
            },
            uri = uri
        )
        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        ServerContent(uri = uri)
    }

}



# KWalletConnect
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.joesteven/kwalletconnect/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.joesteven/kwalletconnect)

KWalletConnect is a Kotlin Multiplatform library for WalletConnect protocol v1.

## Setup

Add the dependency in your common module's commonMain sourceSet

```kotlin
kotlin {
    // ...
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.joesteven:kwalletconnect:$last_version")
            }
        }
    }
}

```

## Usage
### WCConnectionStore
WCConnectionStore is an interface that you need to implement to store the connection data.
You can use any storage you want(SqlDelight database/File System...), but you need to implement the interface.

### WCClient
#### 1.create a WCClient instance, better make it a singleton
```kotlin
 val wcClient = WCClient(store = YourWCConnectionStore())
```

#### 2.create a WCSessionConfig to get the WalletConnect uri
```kotlin
val config = WCSessionConfig(bridge = "https://bridge.walletconnect.org")
val uri = config.uri
```

#### 3.connection
`connect` will return `Result<WCConnection>`, you can use this connection to send and receive messages.
```kotlin
val result = wcClient.connect(
    config = config,
    clientMeta = WCPeerMeta(
        name = "Your Client name",
        description = "Your client description",
        icons = listOf("Your client logo url"),
        url = "Your client website"
    )
)
//disconnect
wcClient.disconnect(connectionId)
```
#### 4.send a message
```kotlin
wcClient.request(
    connectionId = it.id,
    method = "personal_sign",
    params = Json.encodeToJsonElement(
                listOf("0x48656c6c6f2c207765623321", 
                    it.accounts.first(),
                )
            ).jsonArray
)
```
You can also get specified connection or current connections by `wcClient.getConnection(connectionId)` or `wcClient.connections`

### WCServer
#### 1.create a WCServer instance, better make it a singleton
```kotlin
    val wcServer = WCServer(store = YourWCConnectionStore())
```

#### 2.pair with a WalletConnect uri
```kotlin
wcServer.pair(
    uri = uri,
    clientMeta = YourClientMeta
).onSuccess {
    // display a dialog or something for your user to approve or reject the connection
    //onSuccess will return a instance of PairRequest, use this instance to approve or reject connection
}.onFailure {
    // handle error
}
// approve
pairRequest.approve(
    account = listOf("0x1234567890...."),// connected wallet address,
    chainId = 1
)

// reject
pairRequest.reject()

// disconnect
wcServer.disconnect(connectionId)
```
#### 3.handle client request and send response
```kotlin

wcServer.request.collect{    
    // display a dialog or something for your user to respond to the client request
    // request contains connectionId and request method
// you will need connection id and request id to response later
}

// send response
wcServer.response(
    connectionId = request.connectionId,
    requestId = request.method.requestId,
    response = response// JsonElement
)

// send error response
wcServer.errorResponse(
    connectionId = request.connectionId,
    requestId = request.method.requestId,
    errorCode = -32000,// error code here
    errorMessage = "error message here"
)

```

## Proguard
```
-keep class com.mimao.kmp.walletconnect.entity.** { *; }
```

## LICENSE
```
MIT License

Copyright (c) 2022 itsMimao

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
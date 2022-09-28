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
working in progress...

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
package com.mimao.kmp.walletconnect.entity

import kotlinx.serialization.Serializable

private const val JSONRPC_VERSION = "2.0"
@Serializable
internal data class JsonRpcRequest<T>(
    val id: Long,
    val jsonrpc: String = JSONRPC_VERSION,
    val method: String?,
    val params: T
)

@Serializable
internal data class JsonRpcResponse<T>(
    val jsonrpc: String = JSONRPC_VERSION,
    val id: Long,
    val result: T?
)

@Serializable
internal data class JsonRpcErrorResponse(
    val jsonrpc: String = JSONRPC_VERSION,
    val id: Long,
    val error: JsonRpcError
)

@Serializable
internal data class JsonRpcError(
    val code: Long,
    val message: String
)
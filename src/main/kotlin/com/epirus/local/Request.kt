package com.epirus.local

class Request(
    val jsonrpc: String,
    val method: String = "",
    val params: Any = emptyList<String>(),
    val id: Long = 0
)
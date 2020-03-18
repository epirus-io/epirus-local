package com.epirus.local

class Request(
    val jsonrpc: String = "2.0",
    val method: String = "",
    val params: List<String> = emptyList<String>(),
    val id: Long = 0
)
package com.epirus.local

import com.beust.klaxon.Klaxon
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class Server {

    val localLedger = LocalLedger()

    fun start() {
        val server = embeddedServer(Netty, 8080) {
            routing {
                post("/") {
                    try {
                        val jsonRequest: String = call.receive<String>()
                        val jsonResponse: String = processRequest(jsonRequest)
                        call.respondText(jsonResponse, ContentType.Text.Plain)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respondText(
                                "Incorrect request or problem when executing it! Check https://github.com/ethereum/wiki/wiki/JSON-RPC\n",
                                ContentType.Text.Plain
                        )
                    }
                }
            }
        }
        server.start(true)
    }

    private fun processRequest(jsonRequest: String): String {
        val request = JsonParser().parse(jsonRequest)
        val response = makeCall(request)
        return Klaxon().toJsonString(Response(request.id, request.jsonrpc, response))
    }

    fun makeCall(request: Request): Any {
        return when (request.method) {
            "eth_blockNumber" -> localLedger.eth_blockNumber()
            "eth_getTransactionCount" -> localLedger.eth_getTransactionCount(request)
            "eth_getBalance" -> localLedger.eth_getBalance(request)
            "eth_sendTransaction" -> localLedger.eth_sendTransaction(request)
            "eth_sendRawTransaction" -> localLedger.eth_sendRawTransaction(request)
            "eth_estimateGas" -> localLedger.eth_estimateGas(request)
            "eth_getBlockByHash" -> localLedger.eth_getBlockByHash(request)
            "eth_getBlockByNumber" -> localLedger.eth_getBlockByNumber(request)
            "eth_getBlockTransactionCountByHash" -> localLedger.eth_getBlockTransactionCountByHash(request)
            "eth_getBlockTransactionCountByNumber" -> localLedger.eth_getBlockTransactionCountByNumber(request)
            else -> "Not supported yet!"
        }
    }

}

fun main(args: Array<String>) {
    val server: Server = Server()
    server.start()
}

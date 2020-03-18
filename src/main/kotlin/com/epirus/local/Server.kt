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
import org.web3j.abi.datatypes.Address
import org.web3j.crypto.Hash
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedEthereum
import org.web3j.evm.PassthroughTracer

class Server{

    val embeddedEthereum: EmbeddedEthereum = EmbeddedEthereum(Configuration(Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), 10), PassthroughTracer())

    fun start() {
        val server = embeddedServer(Netty, 8080) {
            routing {
                post("/") {
                    try {
                        val req : String = call.receive<String>()

                        val request : Request = Klaxon().parse<Request>(req) ?: throw Exception("Incorrect request: \n $req")
                        val json_output = Klaxon().toJsonString(Response(request.id, request.jsonrpc, makeCall(request)))

                        call.respondText(json_output, ContentType.Text.Plain)
                    } catch (e : Exception) {
                        e.printStackTrace()
                        call.respondText("Incorrect request! Check https://github.com/ethereum/wiki/wiki/JSON-RPC\n", ContentType.Text.Plain)
                    }
                }
            }
        }
        server.start(true)
    }

    fun makeCall(request: Request): String {
        return when (request.method) {
            "eth_blockNumber" -> embeddedEthereum.ethBlockNumber()
            "eth_getTransactionCount" -> embeddedEthereum.getTransactionCount(Address(request.params[0]), request.params[1]).toString()
            "eth_getBalance" -> embeddedEthereum.ethGetBalance(Address(request.params[0]), request.params[1]) ?: "0"
            else -> return "Not supported yet!"
        }
    }

}

fun main(args: Array<String>) {
    val server: Server = Server()
    server.start()
}

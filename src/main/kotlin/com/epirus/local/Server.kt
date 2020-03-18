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

class Server{

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

    fun makeCall(lRequest: Request): String {
        return when (lRequest.method) {
            else -> return "Not supported yet!"
        }
    }

}

fun main(args: Array<String>) {
    val server: Server = Server()
    server.start()
}

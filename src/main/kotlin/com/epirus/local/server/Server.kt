/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.epirus.local.server

import com.epirus.local.ledger.LedgerHelpers
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Application.nettyServer() {

    val localLedger = LedgerHelpers.createLedger(environment.config
            .propertyOrNull("sun.java.command")?.getString())
    val requestHandler = RequestHandler(localLedger)

    install(DefaultHeaders)
    install(CallLogging)
    routing {
        post("/") {
            try {
                val jsonRequest: String = call.receive()
                val jsonResponse: String = requestHandler.processRequest(jsonRequest)
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

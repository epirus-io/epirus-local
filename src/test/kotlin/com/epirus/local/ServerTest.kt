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
package com.epirus.local

import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ServerTest {

    @KtorExperimentalAPI
    @Test
    fun requestTest(): Unit = withTestApplication({ nettyServer() }) {
        handleRequest(HttpMethod.Post, "/") {
            // addHeader(HttpHeaders.ContentType, ContentType.Application.Json.contentType)
            setBody("""{"jsonrpc":"2.0","method":"eth_estimateGas","params":[{
                            "from": "0x43dd200f9bdeffc55203835f21a3126905f4aa92",
                            "to": "0xd46e8dd67c5d32be8058bb8eb970870f07244567",
                            "gasPrice": "0x9184e72a000",
                            "value": "0x9184e72a",
                            "nonce": "0x1",
                            "data": ""}],"id":1}""")
        }.apply {
            assertEquals(
                    """{"id" : 1, "jsonrpc" : "2.0", "result" : "0x0000000000000000000000000000000000000000000000000000000000989682"}""",
                    response.content)
        }
    }
}

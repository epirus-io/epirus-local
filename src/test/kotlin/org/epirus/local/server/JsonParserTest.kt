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
package org.epirus.local.server

import org.junit.Test
import kotlin.test.assertEquals

class JsonParserTest {

    @Test
    fun parseTest() {
        val jsonReq = """{"jsonrpc":"2.0","method":"eth_call","params":[{"
                "from": "0xec4c32516b5b8ab1fbc4e321e9974d94acc39c46","
                "to": "0xd46e8dd67c5d32be8058bb8eb970870f07244567","
                "gas": "0x76c0","
                "gasPrice": "0x9184e72a000",
                "value": "0x9184e72a", "nonce":"0x0",
                "data": ""
                "}, "latest"],"id":1}""".trimIndent()
        val actualReq = JsonParser().parse(jsonReq)

        val expectedParams = HashMap<String, String>()
        expectedParams["from"] = "0xec4c32516b5b8ab1fbc4e321e9974d94acc39c46"
        expectedParams["to"] = "0xd46e8dd67c5d32be8058bb8eb970870f07244567"
        expectedParams["gas"] = "0x76c0"
        expectedParams["gasPrice"] = "0x9184e72a000"
        expectedParams["value"] = "0x9184e72a"
        expectedParams["nonce"] = "0x0"
        expectedParams["data"] = ""
        expectedParams["tag"] = "latest"

        val expectedReq = Request("2.0", "eth_call", expectedParams, 1)

        assertEquals(expectedReq.id, actualReq.id)
        assertEquals(expectedReq.method, actualReq.method)
        assertEquals(expectedReq.params, actualReq.params)
        assertEquals(expectedReq.jsonrpc, actualReq.jsonrpc)
    }
}

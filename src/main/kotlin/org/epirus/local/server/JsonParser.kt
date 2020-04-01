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

import com.beust.klaxon.JsonReader
import com.beust.klaxon.token.LEFT_BRACE
import java.io.StringReader

/* Parsing eth json rpc requests:
    {
        "jsonrpc" : "2.0",
        "method" : "method",
        "params" :
            - can be empty array: []
            - array with fields: ["a", "b"..]
            - array containing one object:  [{"from":"0x1", "to": ...}]
         "id" : 1
     }
 */
class JsonParser {

    @Suppress("UNCHECKED_CAST")
    fun parse(request: String): Request {
        var jsonrpc = ""
        var id: Long = 0
        var method = ""
        val params = arrayListOf(HashMap<String, String>(), mutableListOf<String>())

        JsonReader(StringReader(request)).use { reader ->
            reader.beginObject {
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "jsonrpc" -> jsonrpc = reader.nextString()
                        "id" -> id = reader.nextLong()
                        "method" -> method = reader.nextString()
                        "params" -> reader.beginArray {
                            if (reader.lexer.peek() == LEFT_BRACE) {
                                reader.beginObject {
                                    while (reader.hasNext()) {
                                        when (reader.nextName()) {
                                            "from" -> (params[0] as HashMap<String, String>)["from"] = reader.nextString()
                                            "to" -> (params[0] as HashMap<String, String>)["to"] = reader.nextString()
                                            "gas" -> (params[0] as HashMap<String, String>)["gas"] = reader.nextString()
                                            "gasPrice" -> (params[0] as HashMap<String, String>)["gasPrice"] = reader.nextString()
                                            "value" -> (params[0] as HashMap<String, String>)["value"] = reader.nextString()
                                            "data" -> (params[0] as HashMap<String, String>)["data"] = reader.nextString()
                                            "nonce" -> (params[0] as HashMap<String, String>)["nonce"] = reader.nextString()
                                        }
                                    }
                                }
                                if (reader.hasNext()) (params[0] as HashMap<String, String>)["tag"] = reader.nextString() // Eth_call case
                            } else
                                while (reader.hasNext()) (params[1] as MutableList<String>).add(reader.nextString())
                        }
                        else -> method = "Incorrect request"
                    }
                }
            }
        }
        return if ((params[1] as List<String>).size > 0)
            Request(jsonrpc, method, params[1], id)
        else
            Request(jsonrpc, method, params[0], id)
    }
}

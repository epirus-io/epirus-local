package com.epirus.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
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

    fun parse(request: String): Request {
        var jsonrpc: String = ""
        var id: Long = 0
        var method: String = ""
        var params = arrayListOf<Any>(HashMap<String, String>(), mutableListOf<String>())

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
                                if(reader.hasNext()) (params[0] as HashMap<String, String>)["tag"] = reader.nextString() // Eth_call case
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
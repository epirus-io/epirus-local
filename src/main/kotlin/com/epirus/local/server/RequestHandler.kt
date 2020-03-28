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

import com.beust.klaxon.Klaxon
import com.epirus.local.ledger.LocalLedger

class RequestHandler(private val localLedger: LocalLedger) {

    fun processRequest(jsonRequest: String): String {
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
            // "eth_getBlockByHash" -> localLedger.eth_getBlockByHash(request)
            // "eth_getBlockByNumber" -> localLedger.eth_getBlockByNumber(request)
            "eth_getBlockTransactionCountByHash" -> localLedger.eth_getBlockTransactionCountByHash(request)
            "eth_getBlockTransactionCountByNumber" -> localLedger.eth_getBlockTransactionCountByNumber(request)
            "eth_getTransactionReceipt" -> localLedger.eth_getTransactionReceipt(request)
            "eth_getCode" -> localLedger.eth_getCode(request)
            "eth_call" -> localLedger.eth_call(request)
            else -> "Not supported yet!"
        }
    }
}

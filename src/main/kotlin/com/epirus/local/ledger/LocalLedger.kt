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
package com.epirus.local.ledger

import com.epirus.local.cli.Account
import com.epirus.local.server.Request
import org.hyperledger.besu.ethereum.core.Hash
import org.web3j.abi.datatypes.Address
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedEthereum
import org.web3j.evm.PassthroughTracer
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.net.URL
import java.util.stream.Collectors

class LocalLedger(val accounts: List<Account> = emptyList(), val genesisPath: String) {

    private val embeddedEthereum: EmbeddedEthereum
    init {
        embeddedEthereum =
                EmbeddedEthereum(
                        loadConfig(genesisPath),
                        PassthroughTracer())
    }

    private fun loadConfig(path: String): Configuration {
        return Configuration(Address("0x0"), 0, URL("file:$path"))
    }

    fun eth_blockNumber(): String = embeddedEthereum.ethBlockNumber()

    @Suppress("UNCHECKED_CAST")
    fun eth_getTransactionCount(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.getTransactionCount(Address(request.params[0]),
                if (request.params.getOrNull(1) == null) "latest" else request.params[1])
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getBalance(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetBalance(Address(requestParams[0]),
                if (request.params.getOrNull(1) == null) "latest" else request.params[1]) ?: "0"
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_estimateGas(request: Request): Any {
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        val params = prepareParams(requestParams)
        return embeddedEthereum.estimateGas(Transaction(
                params["from"] as String,
                params["nonce"] as BigInteger,
                params["gasPrice"] as BigInteger,
                params["gas"] as BigInteger,
                params["to"] as String,
                params["value"] as BigInteger,
                params["data"] as String))
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getBlockByHash(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        return embeddedEthereum.ethBlockByHash(requestParams[0], requestParams[1].toBoolean()) ?: "null"
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getBlockByNumber(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        val block: EthBlock.Block? = embeddedEthereum.ethBlockByNumber(requestParams[0].removePrefix("0x"), requestParams[1].toBoolean())
        return block?.toHashMap(requestParams[1].toBoolean()) ?: "null"
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getBlockTransactionCountByHash(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetBlockTransactionCountByHash(Hash.fromHexString(requestParams[0]))
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getBlockTransactionCountByNumber(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetBlockTransactionCountByNumber(requestParams[0].removePrefix("0x").toLong(16))
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_sendRawTransaction(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.processTransaction(requestParams[0])
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_sendTransaction(request: Request): Any {
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.size < 3) return "Insufficient parameters"
        val params = prepareParams(requestParams)
        val rawTransaction = RawTransaction.createTransaction(
                params["nonce"] as BigInteger,
                params["gasPrice"] as BigInteger,
                params["gas"] as BigInteger,
                params["to"] as String,
                params["value"] as BigInteger,
                params["data"] as String)
        val credentials = loadCredentials(requestParams["from"])
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        return embeddedEthereum.processTransaction(hexValue)
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getTransactionReceipt(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.getTransactionReceipt(requestParams[0].removePrefix("0x"))?.toHashMap() ?: "null"
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_getCode(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetCode(Address(requestParams[0].removePrefix("0x")),
                if (request.params.getOrNull(1) == null) "latest" else request.params[1])
    }

    @Suppress("UNCHECKED_CAST")
    fun eth_call(request: Request): Any {
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.size < 3) return "Insufficient parameters"
        return embeddedEthereum.ethCall(Transaction.createEthCallTransaction(
                    requestParams["from"]?.removePrefix("0x"),
                    requestParams["to"]?.removePrefix("0x"),
                    requestParams["data"]?.removePrefix("0x")),
                if (requestParams["tag"] == null) "latest" else requestParams["tag"]!!)
    }

    private fun loadCredentials(address: String?): Credentials {
        val account = accounts.stream()
                .filter { it.address == address }
                .map { it.privateKey }
                .collect(Collectors.toList())

        return if (account.isEmpty())
            throw Exception("Private key not found! Use eth_sendRawTransaction for personal addresses")
        else
            Credentials.create(account[0])
    }

    fun prepareParams(requestParams: HashMap<String, String>): HashMap<String, Any> {
        val from = requestParams["from"]?.removePrefix("0x")
        val nonce =
                BigInteger(
                        (requestParams["nonce"] ?: "0x" + eth_getTransactionCount(
                                Request("2.0", "eth_getTransactionCount",
                                        listOf(requestParams["from"]), 1)).toString())
                                .removePrefix("0x"),
                        16
                )
        val gasPrice =
                BigInteger(
                        (requestParams["gasPrice"] ?: "0x3b9aca00").removePrefix("0x"),
                        16
                ) // gas price or 1GWei
        val value =
                BigInteger(
                        (requestParams["value"] ?: "0x0").removePrefix("0x"),
                        16
                )
        val to = (requestParams["to"] ?: "0x").removePrefix("0x")
        val data = (requestParams["data"] ?: "0x").removePrefix("0x")
        val gas =
                BigInteger(
                        (requestParams["gas"] ?: embeddedEthereum.estimateGas(
                                Transaction(
                                        from,
                                        nonce,
                                        gasPrice,
                                        null,
                                        to,
                                        value,
                                        data
                                ))).removePrefix("0x"),
                        16
                )
        return hashMapOf(
                "from" to from!!,
                "to" to to,
                "data" to data,
                "gas" to gas,
                "gasPrice" to gasPrice,
                "nonce" to nonce,
                "value" to value
        )
    }
}

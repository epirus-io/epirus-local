package com.epirus.local


import org.hyperledger.besu.ethereum.core.Hash
import org.web3j.abi.datatypes.Address
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedEthereum
import org.web3j.evm.PassthroughTracer
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

class LocalLedger {
    val embeddedEthereum: EmbeddedEthereum

    init {
        embeddedEthereum =
                EmbeddedEthereum(
                        loadConfig(),
                        PassthroughTracer())
    }

    private fun loadConfig(): Configuration {
        return Configuration(Address
        ("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), 10)
    }

    fun eth_blockNumber(): String = embeddedEthereum.ethBlockNumber()

    fun eth_getTransactionCount(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        return embeddedEthereum.getTransactionCount(Address(request.params[0]), request.params[1])
    }

    fun eth_getBalance(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        return embeddedEthereum.ethGetBalance(Address(requestParams[0]), requestParams[1]) ?: "0"
    }

    fun eth_sendTransaction(request: Request): Any { // still not working
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.size < 7) return "Insufficient parameters"
        return embeddedEthereum.processTransaction(Transaction(
                requestParams["from"],
                BigInteger(requestParams["nonce"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gasPrice"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gas"]?.removePrefix("0x"), 16),
                requestParams["to"],
                BigInteger(requestParams["value"]?.removePrefix("0x"), 16),
                requestParams["data"]))
    }

    fun eth_estimateGas(request: Request): Any {
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.size < 7) return "Insufficient parameters"
        return embeddedEthereum.estimateGas(Transaction(
                requestParams["from"],
                BigInteger(requestParams["nonce"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gasPrice"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gas"]?.removePrefix("0x"), 16),
                requestParams["to"],
                BigInteger(requestParams["value"]?.removePrefix("0x"), 16),
                requestParams["data"]))
    }

    fun eth_getBlockByHash(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        return embeddedEthereum.ethBlockByHash(requestParams[0], requestParams[1].toBoolean()) ?: "null"
    }

    fun eth_getBlockByNumber(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if (requestParams.size < 2) return "Insufficient parameters"
        val block : EthBlock.Block? = embeddedEthereum.ethBlockByNumber(requestParams[0], requestParams[1].toBoolean())
        return block?.toHashMap(requestParams[1].toBoolean()) ?: "null"
    }

    fun eth_getBlockTransactionCountByHash(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if(requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetBlockTransactionCountByHash(Hash.fromHexString(requestParams[0]))
    }

    fun eth_getBlockTransactionCountByNumber(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if(requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.ethGetBlockTransactionCountByNumber(requestParams[0].removePrefix("0x").toLong(16))
    }
}
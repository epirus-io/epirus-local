package com.epirus.local


import com.epirus.local.cli.Account
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

class LocalLedger(val accounts: MutableList<Account>, val genesisPath: String = "src/main/resources/defaultGenesis.json", val init: Boolean = false) {

    val embeddedEthereum: EmbeddedEthereum
    init {
        embeddedEthereum =
                EmbeddedEthereum(
                        loadConfig(genesisPath),
                        PassthroughTracer())
        if(init)

        }
    }

    private fun loadConfig(path: String): Configuration {
        return Configuration(Address("0x0"), 0, URL("file:$path"))
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

    fun eth_sendRawTransaction(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if(requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.processTransaction(requestParams[0])
    }

    fun eth_sendTransaction(request: Request): Any { // still not working
        val requestParams: HashMap<String, String> = request.params as HashMap<String, String>
        if (requestParams.size < 6) return "Insufficient parameters"
        val rawTransaction = RawTransaction.createTransaction(
                BigInteger(requestParams["nonce"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gasPrice"]?.removePrefix("0x"), 16),
                BigInteger(requestParams["gas"]?.removePrefix("0x"), 16),
                requestParams["to"],
                BigInteger(requestParams["value"]?.removePrefix("0x"), 16),
                requestParams["data"])
        val credentials = loadCredentials(requestParams["from"])
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        return embeddedEthereum.processTransaction(hexValue)
    }

    fun eth_getTransactionReceipt(request: Request): Any {
        val requestParams: List<String> = request.params as List<String>
        if(requestParams.isEmpty()) return "Insufficient parameters"
        return embeddedEthereum.getTransactionReceipt(requestParams[0].removePrefix("0x"))?.toHashMap() ?: "null"
    }


    private fun loadCredentials(address: String?): Credentials {
        val account = accounts.stream().filter{ it.address == address}.map{it.address}.collect(Collectors.toList())

        return if(account.isEmpty())
            throw Exception("Private key not found! Use eth_sendRawTransaction for personal addresses")
        else
            Credentials.create(account[0])
    }
}

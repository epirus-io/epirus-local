package com.epirus.local


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

class LocalLedger(val genesisPath: String = "src/main/resources/defaultGenesis.json") {
    val embeddedEthereum: EmbeddedEthereum

    init {
        embeddedEthereum =
                EmbeddedEthereum(
                        loadConfig(genesisPath),
                        PassthroughTracer())
    }

    private fun loadConfig(path: String): Configuration {
        println("[*] Address: 0xcFC2BE3d4B50E9b2CAFbDa0779722B1620B8A326 : 100 Ether")
        println("-> pkey: a0a66ef7b7aaaca5750b6e9395344f459d6c1839bb324c7920ad97823707a7d8")
        println("[*] Address: 0x7d6DAFe1f8962B6622203Bb6cdFC1631F88Db17c : 100 Ether")
        println("-> pkey: f2b1fe8725429ca6c870adfc15d451e552dc64a488af0743baedec182275bf84")
        return Configuration(Address("0x1"), 0, URL("file:$path"))
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

    private fun loadCredentials(address: String?): Credentials {
        val pkey = when(address){
            "0xcFC2BE3d4B50E9b2CAFbDa0779722B1620B8A326" -> "a0a66ef7b7aaaca5750b6e9395344f459d6c1839bb324c7920ad97823707a7d8"
            "0x7d6DAFe1f8962B6622203Bb6cdFC1631F88Db17c" -> "f2b1fe8725429ca6c870adfc15d451e552dc64a488af0743baedec182275bf84"
            else -> throw Exception("Private key not found! Use eth_sendRawTransaction for personal addresses")
        }
        return Credentials.create(pkey)
    }
}
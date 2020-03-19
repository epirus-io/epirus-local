package com.epirus.local

import org.web3j.protocol.core.methods.response.EthBlock
import kotlin.streams.toList

fun EthBlock.Block.toHashMap(fullTx: Boolean): HashMap<String,Any> {
    val blockMap = HashMap<String, Any>()

    blockMap["number"] = number.toString(16)
    blockMap["hash"] = hash
    blockMap["parentHash"] = parentHash
    blockMap["nonce"] = nonce.toString(16)
    blockMap["sha3Uncles"] = sha3Uncles
    blockMap["logsBloom"] = logsBloom
    blockMap["transactionsRoot"] = transactionsRoot
    blockMap["stateRoot"] = stateRoot
    blockMap["miner"] = miner
    blockMap["difficulty"] = difficulty.toString(16)
    blockMap["totalDifficulty"] = totalDifficulty.toString(16)
    blockMap["extraData"] = extraData
    blockMap["size"] = size.toString(16)
    blockMap["gasLimit"] = gasLimit.toString(16)
    blockMap["gasUsed"] = gasUsed.toString(16)
    blockMap["timestamp"] = timestamp.toString(16)
    blockMap["uncles"] = uncles ?: emptyList<String>()

    blockMap["transactions"] = if(fullTx){
        transactions.stream().map { t ->(t as EthBlock.TransactionObject).toHashMap() }.toList()
    } else {
        transactions.stream().map{t ->  t.get() }.toList()
    }
    return blockMap
}


fun EthBlock.TransactionObject.toHashMap(): HashMap<String, Any> {
    val txMap = HashMap<String, Any>()
    txMap["hash"] = hash
    txMap["nonce"] = nonce.toString(16)
    txMap["blockHash"] = blockHash
    txMap["blockNumber"] = blockNumber.toString(16)
    txMap["transactionIndex"] = transactionIndex.toString(16)
    txMap["from"] = from
    txMap["to"] = to
    txMap["value"] = value.toString(16)
    txMap["gasPrice"] = gasPrice.toString(16)
    txMap["gas"] = gas.toString(16)
    txMap["input"] = input
    txMap["creates"] = creates
    txMap["publicKey"] = publicKey
    txMap["raw"] = raw
    txMap["r"] = r
    txMap["s"] = s
    return txMap
}
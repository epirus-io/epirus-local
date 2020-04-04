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
package org.epirus.local.ledger

import org.epirus.local.cli.Account
import org.epirus.local.cli.GenesisUtils
import org.epirus.local.server.Request
import org.epirus.local.server.RequestHandler
import org.epirus.local.utils.Folders
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Function
import org.web3j.abi.TypeReference
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.math.BigInteger
import org.junit.jupiter.api.Test
import org.web3j.protocol.core.methods.response.TransactionReceipt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LocalLedgerTest {

    private val localLedger: LocalLedger
    private val requestHandler: RequestHandler
    private val ledgerConfiguration: LedgerConfiguration
    private val accounts: List<Account> = GenesisUtils.generateAccounts()

    init {
        val genesisPath = GenesisUtils.createGenesis(Folders.tempBuildFolder().absolutePath, accounts)
        ledgerConfiguration = LedgerConfiguration(genesisPath, accounts)
        localLedger = LocalLedger(ledgerConfiguration)
        requestHandler = RequestHandler(localLedger)
    }

    @Test
    fun eth_blockNumberTest() {
        val response = requestHandler.makeCall(Request("2.0", "eth_blockNumber", listOf(""), 1))
        assertEquals("0x0000000000000000000000000000000000000000000000000000000000000000", response)
    }

    @Test
    fun eth_getBalanceTest() {
        val response = requestHandler.makeCall(Request("2.0", "eth_getBalance", listOf(accounts[0].address, "latest"), 1))
        assertEquals("0x0000000000000000000000000000000000000000000000056bc75e2d63100000", response)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun sendingTransactionTest() {
        val tx = HashMap<String, Any>()
        tx["from"] = accounts[0].address
        tx["to"] = accounts[1].address
        tx["gas"] = "0x7cfd"
        tx["gasPrice"] = "0x31413"
        tx["value"] = "0xfff24f"
        tx["nonce"] = "0x0"
        val txHash = requestHandler.makeCall(Request("2.0", "eth_sendTransaction", tx, 1))

        assertNotNull(txHash)

        assertEquals(BigInteger.ONE, requestHandler.makeCall(
                Request("2.0",
                        "eth_getTransactionCount",
                        listOf(accounts[0].address,
                                "latest"),
                        1
                )))

        val txReceipt = requestHandler.makeCall(
                Request("2.0",
                        "eth_getTransactionReceipt",
                        listOf(txHash as String),
                        1)) as TransactionReceipt?
        assertNotNull(txReceipt)

        assertEquals("0x1", requestHandler.makeCall(Request("2.0",
                "eth_getBlockTransactionCountByHash",
                listOf(
                        txReceipt.blockHash
                ),
                1)))

        assertEquals("0x1", requestHandler.makeCall(Request("2.0",
                "eth_getBlockTransactionCountByNumber",
                listOf("0x1"),
                1)))

        val balance = requestHandler.makeCall(Request("2.0", "eth_getBalance", listOf(accounts[1].address, "latest"), 1))
        assertEquals("0x0000000000000000000000000000000000000000000000056bc75e2d640ff24f", balance)
    }

    @Test
    fun eth_estimateGasTest() {
        val transactionDetails = HashMap<String, String>()
        transactionDetails["from"] = "0xb60e8dd61c5d32be8058bb8eb970870f07233155"
        transactionDetails["to"] = "0xb60e8dd61c5d32be8058bb8eb970870f07233156"
        transactionDetails["gas"] = "0x341cd"
        transactionDetails["gasPrice"] = "0x3241431"
        transactionDetails["value"] = "0x3431cde"
        transactionDetails["nonce"] = "0x0"
        transactionDetails["data"] = "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675"

        val response = requestHandler.makeCall(Request("2.0", "eth_estimateGas", transactionDetails, 1))
        assertEquals("0x00000000000000000000000000000000000000000000000000000000000341ce", response)
    }

    @Test
    fun eth_sendRawTransactionTest() {
        val tx = HashMap<String, String>()
        tx["from"] = accounts[2].address
        tx["to"] = accounts[3].address
        tx["gas"] = "0x7cfd"
        tx["gasPrice"] = "0x31413"
        tx["value"] = "0xfff24f"
        tx["nonce"] = "0x0"
        tx["data"] = ""
        val rawTransaction = RawTransaction.createTransaction(
                BigInteger(tx["nonce"]?.removePrefix("0x"), 16),
                BigInteger(tx["gasPrice"]?.removePrefix("0x"), 16),
                BigInteger(tx["gas"]?.removePrefix("0x"), 16),
                tx["to"],
                BigInteger(tx["value"]?.removePrefix("0x"), 16),
                tx["data"])
        val credentials = Credentials.create(accounts[2].privateKey)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)

        val txHash = requestHandler.makeCall(Request("2.0", "eth_sendRawTransaction", listOf<String>(hexValue), 1))

        assertNotNull(txHash)

        assertEquals(BigInteger.ONE, requestHandler.makeCall(
                Request("2.0",
                        "eth_getTransactionCount",
                        listOf(accounts[2].address,
                                "latest"),
                        1
                )))

        val balance = requestHandler.makeCall(Request("2.0", "eth_getBalance", listOf(accounts[3].address, "latest"), 1))
        assertEquals("0x0000000000000000000000000000000000000000000000056bc75e2d640ff24f", balance)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun deployingContractTest() {

        // Deploying the contract
        val tx = HashMap<String, String>()
        tx["from"] = accounts[5].address
        tx["data"] = "0x608060405234801561001057600080fd5b5060bf8061001f6000396000f30060806040526004361060485763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416632ca832648114604d578063f2c9ecd8146064575b600080fd5b348015605857600080fd5b5060626004356088565b005b348015606f57600080fd5b506076608d565b60408051918252519081900360200190f35b600055565b600054905600a165627a7a7230582072c890936b2bc717b79b9bbca671d8a983b98822147b264ff0c74766597a9d8f0029"
        tx["gas"] = "0x131840"
        tx["gasPrice"] = "0x131840000"
        tx["value"] = "0x0"
        tx["nonce"] = "0x0"
        val hash: String = requestHandler.makeCall(Request("2.0", "eth_sendTransaction", tx, 1)) as String

        // Getting transaction receipt
        val receipt = requestHandler.makeCall(Request("2.0", "eth_getTransactionReceipt", listOf(hash), 1)) as TransactionReceipt
        assertNotNull(receipt)

        // Calling newNumber function using normal transaction
        val functionConstruct = Function("newNumber", listOf(org.web3j.abi.datatypes.generated.Int256(1)), listOf())
        val txConstruct = FunctionEncoder.encode(functionConstruct)
        val tx2 = HashMap<String, String>()
        tx2["from"] = accounts[5].address
        tx2["to"] = receipt.contractAddress
        tx2["data"] = txConstruct
        requestHandler.makeCall(Request("2.0", "eth_sendTransaction", tx2, 1)) as String

        // Call getNumber using eth_call
        val function = Function("getNumber", mutableListOf(), listOf(object : TypeReference<org.web3j.abi.datatypes.generated.Int256?>() {}))
        val txData = FunctionEncoder.encode(function)
        val call = HashMap<String, String>()
        call["from"] = accounts[5].address
        call["to"] = receipt.contractAddress as String
        call["data"] = txData
        call["tag"] = "latest"
        val result = requestHandler.makeCall(Request("2.0", "eth_call", call, 1))
        assertEquals("0x0000000000000000000000000000000000000000000000000000000000000001", result)

        // Getting code
        val code = requestHandler.makeCall(Request("2.0", "eth_getCode", listOf(receipt.contractAddress, "latest"), 1))
        assertEquals("0x60806040526004361060485763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416632ca832648114604d578063f2c9ecd8146064575b600080fd5b348015605857600080fd5b5060626004356088565b005b348015606f57600080fd5b506076608d565b60408051918252519081900360200190f35b600055565b600054905600a165627a7a7230582072c890936b2bc717b79b9bbca671d8a983b98822147b264ff0c74766597a9d8f0029", code)
    }
}

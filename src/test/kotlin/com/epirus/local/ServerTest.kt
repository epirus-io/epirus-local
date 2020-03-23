package com.epirus.local

import com.epirus.local.cli.Account
import com.epirus.local.cli.CreateCmd
import org.junit.AfterClass
import java.io.File
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ServerTest {

    private val server : Server
    val accounts: List<Account>
    val genesis : String
    init{
        val createCmd = CreateCmd()
        createCmd.directory = "src/test/resources/"
        accounts = createCmd.generateAccounts()
        genesis = createCmd.createGenesis(accounts)
        server = Server(LocalLedger(accounts = accounts, genesisPath = genesis))
        File(genesis).delete()
    }

    @Test
    fun eth_blockNumberTest(){
        val response = server.makeCall(Request("2.0", "eth_blockNumber", listOf<String>(""), 1))
        assertEquals( "0x0000000000000000000000000000000000000000000000000000000000000000", response)
    }

    @Test
    fun eth_getBalanceTest(){
        val response = server.makeCall(Request("2.0", "eth_getBalance", listOf<String>(accounts[0].address, "latest"), 1))
        assertEquals("0x0000000000000000000000000000000000000000000000056bc75e2d63100000", response)
    }

    @Test
    fun eth_sendTransaction(){
        val tx = HashMap<String, Any>()
        tx["from"] = accounts[0].address
        tx["to"] = accounts[1].address
        tx["gas"] = "0x7cfd"
        tx["gasPrice"] = "0x31413"
        tx["value"] = "0xfff24f"
        tx["data"] = ""
        tx["nonce"] = "0x0"
        val txHash = server.makeCall(Request("2.0", "eth_sendTransaction", tx, 1))

        assertNotNull(txHash)

        assertEquals(BigInteger.ONE, server.makeCall(
                Request("2.0",
                        "eth_getTransactionCount",
                        listOf<String>(accounts[0].address,
                                "latest"),
                        1
                )))

        val txReceipt = server.makeCall(
                Request("2.0",
                        "eth_getTransactionReceipt",
                        listOf<String>(txHash as String),
                        1))
        assertNotNull(txReceipt)

        assertEquals("0x1", server.makeCall(Request("2.0",
                "eth_getBlockTransactionCountByHash",
                listOf<String>(
                        (txReceipt as HashMap<String, String>)["blockHash"]!!
                ),
                1)))

        assertEquals("0x1", server.makeCall(Request("2.0",
                "eth_getBlockTransactionCountByNumber",
                listOf<String>("0x1"),
                1)))

    }

    @Test
    fun eth_estimateGasTest(){
        val transactionDetails = HashMap<String, String>()
        transactionDetails["from"] = "0xb60e8dd61c5d32be8058bb8eb970870f07233155"
        transactionDetails["to"] = "0xb60e8dd61c5d32be8058bb8eb970870f07233156"
        transactionDetails["gas"] = "0x341cd"
        transactionDetails["gasPrice"] = "0x3241431"
        transactionDetails["value"] = "0x3431cde"
        transactionDetails["nonce"] = "0x0"
        transactionDetails["data"] = "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675"

        val response = server.makeCall(Request("2.0", "eth_estimateGas", transactionDetails, 1))
        assertEquals("0x00000000000000000000000000000000000000000000000000000000000341ce", response)
    }


    @Test
    fun eth_getBlockByHashTest(){ // to be done after getting send transaction to work

    }

    @Test
    fun eth_sendRawTransactionTest() {


    }




}

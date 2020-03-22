package com.epirus.local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ServerTest {
/*
    private val server : Server

    init{
        server = Server(LocalLedger(accounts = accounts))
    }

    @Test
    fun eth_blockNumberTest(){
        val response = server.makeCall(Request("2.0", "eth_blockNumber", listOf<String>(""), 1))
        assertEquals( "0x0000000000000000000000000000000000000000000000000000000000000000", response)
    }

    @Test
    fun eth_getBalanceTest(){
        val response = server.makeCall(Request("2.0", "eth_getBalance", listOf<String>("0xc94770007dda54cF92009BFF0dE90c06F603a09f", "latest"), 1))
        //assertEquals("0x0000000000000000000000000000000000000000000000008ac7230489e80000", response)
    }

    @Test
    fun eth_getTransactionCountTest(){
        //val response = server.makeCall(Request("2.0", "eth_getTransactionCount", listOf<String>("0xc94770007dda54cF92009BFF0dE90c06F603a09f", "latest"), 1))
        //assertEquals(BigInteger.ZERO, response)
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
    fun eth_getBlockByNumberTest(){
        val actual = server.makeCall(Request("2.0", "eth_getBlockByNumber", listOf<String>("0", "true"), 1))
        assertNotEquals("null", actual)
    }

    @Test
    fun eth_getBlockByHashTest(){ // to be done after getting send transaction to work

    }

    @Test
    fun eth_sendRawTransactionTest() {


    }


 */

}

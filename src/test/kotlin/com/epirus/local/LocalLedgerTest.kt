package com.epirus.local

import org.junit.Before
import org.junit.BeforeClass
import org.web3j.evm.EmbeddedEthereum
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LocalLedgerTest {

    private val server : Server

    init{
        server = Server()
    }

    @Test
    fun eth_blockNumberTest(){
        val response = server.makeCall(Request("2.0", "eth_blockNumber", listOf<String>(""), 1))
        assertEquals(response, "0x0000000000000000000000000000000000000000000000000000000000000000")
    }

    @Test
    fun eth_getBalanceTest(){
        val response = server.makeCall(Request("2.0", "eth_getBalance", listOf<String>("0xc94770007dda54cF92009BFF0dE90c06F603a09f", "latest"), 1))
        assertEquals(response, "0x0000000000000000000000000000000000000000000000008ac7230489e80000")
    }
}

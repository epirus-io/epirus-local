package com.epirus.local

import com.epirus.local.cli.EpirusLocalCLI
import com.fasterxml.jackson.databind.node.ObjectNode
import org.hyperledger.besu.config.JsonUtil
import org.web3j.abi.datatypes.Address
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedEthereum
import org.web3j.evm.PassthroughTracer
import picocli.CommandLine
import java.io.File
import java.net.URL

fun main(args: Array<String>) {
    CommandLine(EpirusLocalCLI()).execute(*args)

}
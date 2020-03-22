package com.epirus.local

import com.epirus.local.cli.EpirusLocalCLI
import picocli.CommandLine

fun main(args: Array<String>) {

    CommandLine(EpirusLocalCLI()).execute(*args)
    /*
    val localLedger = LocalLedger()
    val server: Server = Server(localLedger)
    server.start()

     */
}
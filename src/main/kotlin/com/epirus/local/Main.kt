package com.epirus.local

import com.epirus.local.cli.EpirusLocalCLI
import picocli.CommandLine

fun main(args: Array<String>) {
    if(args.isEmpty()) CommandLine(EpirusLocalCLI()).execute("help")
    else CommandLine(EpirusLocalCLI()).execute(*args)
}
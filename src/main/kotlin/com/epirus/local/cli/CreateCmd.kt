package com.epirus.local.cli

import com.beust.klaxon.Klaxon
import com.epirus.local.LocalLedger
import com.epirus.local.Server
import org.web3j.crypto.Credentials
import picocli.CommandLine
import java.io.File
import java.math.BigInteger
import java.util.concurrent.Callable

@CommandLine.Command(name = "create", description = arrayOf("Creates a new configuration for epirus-local client"))
class CreateCmd : Callable<kotlin.Int> {

    @CommandLine.Option(names = arrayOf("-d", "--directory"),
            description = arrayOf("specify the directory of the output configuration"),
            required = true)
    var directory: String = ""

    @CommandLine.Option(names = arrayOf("-g", "--genesis"),
            description = arrayOf("specify the directory of the genesis file to use"))
    var genesisFile: String = ""

    @CommandLine.Option(names = arrayOf("-w", "--workspace"),
            description = arrayOf("specify the workspace name of the local ledger"),
            required = true)
    var workspace: String = ""

    override fun call(): kotlin.Int {
        val configPath: String = directory.removeSuffix("/") + "/" + workspace + ".json"
        val configFile = File(configPath)
        if(configFile.exists()){
            throw Exception("Workspace already exists! change directory or workspace name.")
        }
        if(!configFile.createNewFile())
            throw Exception("Couldnt create file! check permissions.")
        val accounts = generateAccountsFile(configFile)
        val localLedger = if (genesisFile == "") LocalLedger(accounts)
            else {
                val gen = copyGenesis(genesisFile)
                LocalLedger(accounts, gen)
        }
        Server(localLedger).start()
        return 0
    }

    private fun copyGenesis(genesisFile: String): String {
        val genesis = File(genesisFile)
        if (!genesis.exists()) throw Exception("Genesis file doesn't exist!")
        val genString = genesis.readText()
        val localGen = File(directory.removeSuffix("/") + "/" + workspace + "-genesis.json")
        localGen.createNewFile()
        localGen.writeText(genString)
        return localGen.path
    }

    private fun generateAccountsFile(configFile: File) : MutableList<Account> {
        val accounts = mutableListOf<Account>()
        val charpool : List<Char> = ('a'..'f')+('0'..'9')

        for(i in 1..10){
            val privateKey: String = (1..64)
                    .map{i-> kotlin.random.Random.nextInt(0, charpool.size)}
                    .map(charpool::get)
                    .joinToString("")
            val creds = Credentials.create(privateKey)
            accounts.add(Account(creds.address, BigInteger.valueOf(100),privateKey))
        }

        val jsonString = Klaxon().toJsonString(LedgerAccounts(workspace, accounts))
        configFile.writeText(jsonString)
        return accounts.toMutableList()
    }
}
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
package com.epirus.local.ledger

import com.epirus.local.cli.Account
import org.slf4j.LoggerFactory
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import java.io.File

class LedgerHelpers {
    companion object {

        fun createGenesis(directory: String, accounts: List<Account>): String {
            val genesis = File(directory.removeSuffix("/") + "/genesis.json")
            genesis.createNewFile()
            var json = """{
                  "comment": "Automatically generated genesis. DO NOT MODIFY!",
                  "config": {
                    "chainId": 999,
                    "constantinopleFixBlock": 0,
                    "ethash": {
                      "fixeddifficulty": 100
                    }
                  },
                  "gasLimit": "0x1000000",
                  "difficulty": "0x10000",
                  "alloc": {""".trimIndent()

            accounts.stream()
                    .forEach { t -> json += "\"" + t.address + """":{"balance" : "0x56BC75E2D63100000"},""" }

            json = json.removeSuffix(",") + "}}"
            genesis.writeText(json)
            return genesis.absolutePath
        }

        fun generateAccounts(): List<Account> {
            val accounts = mutableListOf<Account>()

            (1..10).forEach { _ ->
                val ecKeyPair = Keys.createEcKeyPair()

                val creds = Credentials.create(ecKeyPair.privateKey.toString(16))
                accounts.add(Account(creds.address, ecKeyPair.privateKey.toString(16)))
            }
            return accounts.toMutableList()
        }

        fun createLedger(command: String?): LocalLedger {

            val config = parseArguments(command)

            val directory = config.directory ?: "."
            val port = config.port ?: "8080"
            val host = config.host ?: "127.0.0.1"
            val genesisPath = config.genesis

            var log: String
            val localLedger = if (genesisPath.isNullOrBlank()) {
                val accounts = generateAccounts()
                val genesis = createGenesis(directory, accounts)
                log = """-> Starting ledger with generated genesis file: $genesis
            -> chainID = 1
            -> Port = $port
            -> Host = $host"""
                accounts.stream().forEach { t -> log += "\n[*] ${t.address} : 100 eth\n\tPrivate key: ${t.privateKey}" }
                LocalLedger(accounts, genesis)
            } else {
                log = """-> Starting ledger with genesis file: $genesisPath
            -> Port = $port
            -> Host = $host""".trimIndent()
                LocalLedger(genesisPath = genesisPath)
            }

            val logger = LoggerFactory.getLogger(LedgerHelpers::class.java)
            logger.info(log)

            return localLedger
        }

        fun parseArguments(command: String?): Configuration {

            val splitCommand = command?.split(" ")
            val arguments = HashMap<String, String?>()

            splitCommand?.stream()?.forEach { s ->
                run {
                    if (s.startsWith("-p") || s.startsWith("--port")) arguments["port"] = s.split("=").getOrNull(1)
                    else if (s.startsWith("-d") || s.startsWith("--directory")) arguments["directory"] = s.split("=").getOrNull(1)
                    else if (s.startsWith("-h") || s.startsWith("--host")) arguments["host"] = s.split("=").getOrNull(1)
                    else if (s.startsWith("-g") || s.startsWith("--genesis")) arguments["genesis"] = s.split("=").getOrNull(1)
                }
            }

            return Configuration(
                    arguments["genesis"],
                    arguments["port"]?.toInt(),
                    arguments["host"],
                    arguments["directory"])
        }
    }
}

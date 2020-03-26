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
package com.epirus.local.cli

import com.epirus.local.LocalLedger
import com.epirus.local.Server
import org.web3j.crypto.Credentials
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(name = "create", description = arrayOf("Creates a new configuration for epirus-local client"))
class CreateCmd : Callable<kotlin.Int> {

    @CommandLine.Option(names = arrayOf("-d", "--directory"),
            description = arrayOf("specify the directory of the output configuration"),
            defaultValue = ".")
    var directory: String = "."

    @CommandLine.Option(names = arrayOf("-g", "--genesis"),
            description = arrayOf("specify the directory of the genesis file to use"))
    var genesisFile: String = ""

    override fun call(): kotlin.Int {
        val localLedger = if (genesisFile.isBlank()) {
            val accounts = generateAccounts()
            val genesis = createGenesis(accounts)
            LocalLedger(accounts, genesis)
        } else {
                LocalLedger(genesisPath = genesisFile)
        }
        Server(localLedger).start()
        File(genesisFile).delete()
        return 0
    }

    fun createGenesis(accounts: List<Account>): String {
        val genesis = File(directory.removeSuffix("/") + "/genesis.json")
        genesis.createNewFile()
        var json = "{\n" +
                "  \"config\": {\n" +
                "    \"chainId\": 999,\n" +
                "    \"constantinopleFixBlock\": 0,\n" +
                "    \"ethash\": {\n" +
                "      \"fixeddifficulty\": 100\n" +
                "    }\n" +
                "  },\n" +
                "  \"gasLimit\": \"0x1000000\",\n" +
                "  \"difficulty\": \"0x10000\"," +
                "\"alloc\": {"

        accounts.stream()
                .forEach { t -> json += "\"" + t.address + "\":{\"balance\":\"0x56BC75E2D63100000\"}," }

        json = json.removeSuffix(",") + "}}"
        genesis.writeText(json)
        return genesis.absolutePath
    }

    fun generateAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()
        val charpool: List<Char> = ('a'..'f') + ('0'..'9')

        (1..10).forEach {
            val privateKey: String = (1..64)
                    .map { kotlin.random.Random.nextInt(0, charpool.size) }
                    .map(charpool::get)
                    .joinToString("")
            val creds = Credentials.create(privateKey)
            accounts.add(Account(creds.address, privateKey))
        }
        return accounts.toMutableList()
    }
}

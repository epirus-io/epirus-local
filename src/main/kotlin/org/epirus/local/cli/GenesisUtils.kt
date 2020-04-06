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
package org.epirus.local.cli

import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import java.io.File

object GenesisUtils {

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

        accounts.forEach { t -> json += "\"" + t.address + """":{"balance" : "0x56BC75E2D63100000"},""" }

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
        return accounts
    }
}

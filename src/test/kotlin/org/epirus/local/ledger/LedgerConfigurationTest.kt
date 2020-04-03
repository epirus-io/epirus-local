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
package org.epirus.local.ledger

import org.epirus.local.utils.Folders
import org.junit.jupiter.api.Test
import java.io.File

class LedgerConfigurationTest {

    private val ledgerConfig = LedgerConfiguration(directory = Folders.tempBuildFolder().absolutePath)
    @Test
    fun generateAccountsTest() {
        val accounts = ledgerConfig.generateAccounts()
        assert(accounts.isNotEmpty())
    }

    @Test
    fun createGenesisTest() {
        val genesis = ledgerConfig.createGenesis()
        val genesisFile = File(genesis)
        assert(genesisFile.exists())
        genesisFile.delete()
    }
}

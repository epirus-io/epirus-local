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
package com.epirus.local

import com.epirus.local.cli.Account
import com.epirus.local.cli.CreateCmd
import java.io.File

class ServerTest {

    private val server: Server
    private val accounts: List<Account>
    private val genesis: String
    private val localLedger: LocalLedger
    private val createCmd: CreateCmd = CreateCmd()
    private val requestHandler: RequestHandler
    init {
        createCmd.directory = "src/test/resources/"
        accounts = createCmd.generateAccounts()
        genesis = createCmd.createGenesis(accounts)
        localLedger = LocalLedger(accounts = accounts, genesisPath = genesis)
        server = Server(localLedger)
        requestHandler = server.requestHandler
        File(genesis).delete()
    }
/*
    @Test

    fun requestTest() = withTestApplication({ server.nettyApplication(localLedger) }) {
        handleRequest(HttpMethod.Post, "/") {
            //addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody("{\"jsonrpc\":\"2.0\"," +
                    "\"method\":\"eth_getBalance\"," +
                    "\"params\":[\"${accounts[0].address}\"," +
                    " \"latest\"],\"id\":1}")
        }.apply {
            //assertEquals(
            //        "{\"id\" : 1, \"jsonrpc\" : \"2.0\", \"result\" : \"0x0000000000000000000000000000000000000000000000056bc75e2d63100000\"}",
            //        response.content)
        }
    }
     */
}

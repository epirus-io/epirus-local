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

import org.epirus.local.ledger.LedgerConfiguration
import org.epirus.local.server.nettyServer
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.util.KtorExperimentalAPI
import picocli.CommandLine
import picocli.CommandLine.Command
import sun.misc.Signal
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

@Command(name = "load",
        description = ["Load a previous configuration for epirus-local client",
            """Example: epirus-local load -g=folder/genesis.json"""])
class LoadCmd : Callable<Int> {

    @CommandLine.Option(names = ["-g", "--genesis"],
            description = ["specify the genesis file"],
            defaultValue = "./genesis.json")
    var genesisFilePath: String = "./genesis.json"

    @CommandLine.Option(names = ["-p", "--port"],
            description = ["specify the port to run the client on"])
    var cliPort: Int = 8080

    @CommandLine.Option(names = ["-h", "--host"],
            description = ["specify the host to run the client on"])
    var cliHost: String = "0.0.0.0"

    private lateinit var client: NettyApplicationEngine

    @KtorExperimentalAPI
    override fun call(): Int {

        val ledgerConfiguration = LedgerConfiguration(
                genesis = genesisFilePath)
        val env = applicationEngineEnvironment {
            connector {
                host = cliHost
                port = cliPort
            }
            module {
                nettyServer(ledgerConfiguration)
            }
        }
        client = embeddedServer(Netty, env)
        client.start(true)

        Signal.handle(Signal("INT")) { client.stop(1, 5, TimeUnit.SECONDS) }
        return 0
    }
}

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

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(name = "load", description = ["Load a previous configuration for epirus-local client"])
class LoadCmd : Callable<Int> {

    @CommandLine.Option(names = ["-g", "--genesis"],
            description = ["specify the genesis file"],
            defaultValue = ".")
    var directory: String = "."

    // FIXME: when specified => network permissions required
    @CommandLine.Option(names = ["-p", "--port"],
            description = ["specify the port to run the client on"],
            hidden = true)
    var port: Int = 8080

    // FIXME: when specified => network permissions required
    @CommandLine.Option(names = ["-h", "--host"],
            description = ["specify the host to run the client on"],
            hidden = true)
    var host: String = "0.0.0.0"

    override fun call(): Int {
        embeddedServer(Netty, commandLineEnvironment(arrayOf())).start()
        return 0
    }
}

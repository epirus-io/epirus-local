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

import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(name = "epirus-local",
        version = ["epirus-local 1.0"],
        description = ["\n" +
                "\n" +
                "____ ___  _ ____ _  _ ____    _    ____ ____ ____ _    \n" +
                "|___ |__] | |__/ |  | [__     |    |  | |    |__| |    \n" +
                "|___ |    | |  \\ |__| ___]    |___ |__| |___ |  | |___ \n" +
                "                                                       \n"],
        subcommands = [StartCmd::class, LoadCmd::class, CommandLine.HelpCommand::class]
)
class EpirusLocalCLI : Callable<Int> {
    override fun call(): Int {
        return 0
    }
}

package com.epirus.local.cli

import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "epirus-local",
        version = arrayOf("epirus-local 1.0"),
        description = arrayOf("\n" +
                "\n" +
                "____ ___  _ ____ _  _ ____    _    ____ ____ ____ _    \n" +
                "|___ |__] | |__/ |  | [__     |    |  | |    |__| |    \n" +
                "|___ |    | |  \\ |__| ___]    |___ |__| |___ |  | |___ \n" +
                "                                                       \n"),
        subcommands = [CreateCmd::class, LoadCmd::class, picocli.CommandLine.HelpCommand::class]
)
class EpirusLocalCLI : Callable<kotlin.Int> {


    override fun call(): kotlin.Int {
        return 0
    }

}
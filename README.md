# Epirus-local
Epirus-local is a local ethereum client, similar to what Ganache provides, written in Kotlin.

## Features
- Allows json-rpc interactions with a local ethereum blockchain.
- Generates a default genesis file containing 10 ethereum accounts or lets you use your own genesis.
- Provides a CLI to run the client.
- Handles these operations:
    - eth_blockNumber
    - eth_getTransactionCount
    - eth_getBalance
    - eth_sendTransaction
    - eth_sendRawTransaction
    - eth_estimateGas
    - eth_getBlockTransactionCountByHash
    - eth_getBlockTransactionCountByNumber
    - eth_getTransactionReceipt
    - eth_getCode
    - eth_call
    
## How to build
`git clone https://github.com/epirus-io/epirus-local && cd epirus-local`
    
`./gradlew installDist`
    
#### Run in linux:
`build/install/epirus-local/bin/epirus-local [command]`
    
#### Run in windows
`build\install\epirus-local\bin\epirus-local [command]`

## Commands

So far, epirus-local supports two subcommands:

- start: generates a new genesis file with 10 accounts filled with 100 ether via providing a directory where to put it.
    
- load: loads a pre-existing genesis-file from a certain path.
    

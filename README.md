# Epirus-local

.. image:: https://api.travis-ci.org/web3j/web3j-docs.svg?branch=master
   :target: https://docs.web3j.io
   :alt: Documentation Status

.. image:: https://travis-ci.org/epirus-io/epirus-local.svg?branch=master
   :target: https://travis-ci.org/epirus-io/epirus-local
   :alt: Build Status

.. image:: https://codecov.io/gh/epirus-io/epirus-local/branch/master/graph/badge.svg
   :target: https://codecov.io/gh/epirus-io/epirus-local
   :alt: codecov

.. image:: https://badges.gitter.im/web3j/web3j.svg
   :target: https://gitter.im/web3j/web3j
   :alt: Join the chat at https://gitter.im/web3j/web3j

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
    
#### Run in Linux:
`build/install/epirus-local/bin/epirus-local [command]`
    
#### Run in Windows
`build\install\epirus-local\bin\epirus-local [command]`

## Commands

So far, epirus-local supports two subcommands:

- start: generates a new genesis file with 10 accounts filled with 100 ether via providing a directory where to put it.
    
- load: loads a pre-existing genesis-file from a certain path.
    

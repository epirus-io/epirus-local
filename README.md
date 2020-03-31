# Epirus-local
Epirus-local is a local ethereum client, similar to what Ganache provides, written in Kotlin.

# Features
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
    

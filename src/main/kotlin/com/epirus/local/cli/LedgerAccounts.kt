package com.epirus.local.cli

import java.math.BigInteger

class LedgerAccounts(val workspace: String,
                     val accounts: List<Account>) {

}

class Account(val address: String, val balance: BigInteger, val privateKey: String) {
}

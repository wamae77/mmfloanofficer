package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class WalletAccount(
    val accountId: Int,
    val accountName: String,
    val accountNo: String,
    val availableBalance: String,
    val currency: String,
    val currentBalance: String,
    val dateOpened: String,
    val isTransactional: Int,
    val lastAmountTransacted: String,
    val lastSavingDate: Any,
    val productId: Int
){
    override fun toString(): String {
        val accNo = accountNo.replace("(?<=.{2}).(?=.{2})".toRegex(), "*")
        return "$accountName - $accNo"
    }
}
package com.deefrent.rnd.fieldapp.models.loans

import androidx.annotation.Keep

@Keep
data class Transaction(
    val balance: String,
    val credit: String,
    val currency: String,
    val debit: String,
    val notes: String,
    val refNo: String,
    val transactionDate: String,
    val transactionType: String
)
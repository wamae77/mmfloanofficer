package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class PostBillPaymentResponse(
    val `data`: PostBillPaymentData,
    val message: String,
    val status: Int
)

@Keep
data class PostBillPaymentData(
    val defaultCurrency: String,
    val transactionCode: String,
    val walletBalance: String
)
package com.deefrent.rnd.fieldapp.models.merchantAgentReport

import androidx.annotation.Keep

@Keep
data class Transaction(
    val date: String,
    val amount: Double
)
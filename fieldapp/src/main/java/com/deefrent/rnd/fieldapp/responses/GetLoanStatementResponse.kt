package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class GetLoanStatementResponse(
    val `data`: LoanStatementData,
    val message: String,
    val status: Int
)
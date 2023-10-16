package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class OnboardCustomerResponse(
    val accountNo: Int,
    val message: String,
    val status: String
)
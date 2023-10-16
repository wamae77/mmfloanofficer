package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class OnboardMerchantAgentResponse(
    val accountNo: Long,
    val message: String,
    val status: String
)
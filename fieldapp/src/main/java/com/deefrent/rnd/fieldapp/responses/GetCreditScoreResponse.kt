package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class GetCreditScoreResponse(
    val `data`: String,
    val message: String,
    val status: Int
)
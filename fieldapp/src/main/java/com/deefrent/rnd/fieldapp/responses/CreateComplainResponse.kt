package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class CreateComplainResponse(
    val message: String,
    val status: String
)
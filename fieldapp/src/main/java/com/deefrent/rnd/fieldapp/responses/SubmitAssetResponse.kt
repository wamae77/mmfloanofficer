package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class SubmitAssetResponse(
    val message: String,
    val status: String
)
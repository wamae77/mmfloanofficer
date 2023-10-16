package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class XDSResponse(
    @SerializedName("data")
    val `data`: String, // Good
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)
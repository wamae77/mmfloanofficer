package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChangePinResponse(
    @SerializedName("data")
    val `data`: ChangePinData,
    @SerializedName("message")
    val message: String, // Successfully Verified
    @SerializedName("status")
    val status: Int // 1
)

@Keep
data class ChangePinData(
    @SerializedName("token")
    val token: String // 5036

)
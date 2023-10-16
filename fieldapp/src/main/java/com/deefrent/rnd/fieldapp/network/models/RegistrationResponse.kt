package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegistrationResponse(
    @SerializedName("data")
    val `data`: RegistrationData,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
) {
    @Keep
    data class RegistrationData(
        @SerializedName("form_id")
        val formId: Int // 206
    )
}
package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Created by Tom Munyiri on 13/09/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

@Keep
data class LogoutResponse(
    @SerializedName("data")
    val `data`: Any,
    @SerializedName("message")
    val message: String, // Login Success
    @SerializedName("status")
    val status: Int // 1
)

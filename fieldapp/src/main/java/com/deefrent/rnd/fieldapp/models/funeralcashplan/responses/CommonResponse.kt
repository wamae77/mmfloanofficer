package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Tom Munyiri on 19/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

data class CommonResponse(
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

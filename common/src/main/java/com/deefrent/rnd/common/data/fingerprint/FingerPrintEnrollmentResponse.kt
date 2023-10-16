package com.deefrent.rnd.common.data.fingerprint


import com.google.gson.annotations.SerializedName

data class FingerPrintEnrollmentResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("enrollment")
        val enrollment: String?,
        @SerializedName("user_uid")
        val userUid: String?,
    )
}
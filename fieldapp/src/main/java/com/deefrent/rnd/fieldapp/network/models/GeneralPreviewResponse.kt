package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeneralPreviewResponse(
    @SerializedName("data")
    val `data`: GeneralPreviewData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class GeneralPreviewData(
    @SerializedName("charges")
    val charges: String,
    @SerializedName("exerciseDuty")
    val exerciseDuty: String,
    @SerializedName("formId")
    val formId: Int
)

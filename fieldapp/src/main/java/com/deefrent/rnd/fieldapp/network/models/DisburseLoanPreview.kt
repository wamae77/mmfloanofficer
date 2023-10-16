package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DisburseLoanPreview(
    @SerializedName("data")
    val `data`: DisburseLoanData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class DisburseLoanData(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("exerciseDuty")
    val exerciseDuty: String,
    @SerializedName("fee")
    val fee: String
)

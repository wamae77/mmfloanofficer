package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TellerAccountStatmentResponse(
    @SerializedName("data")
    val `data`: List<TellerAccountStatmentData>,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

@Keep
data class TellerAccountStatmentData(
    @SerializedName("amount")
    val amount: String, // 70
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("transactionDate")
    val transactionDate: String, // 13/04/2022
    @SerializedName("transactionType")
    val transactionType: String // CASH WITHDRAWAL
)

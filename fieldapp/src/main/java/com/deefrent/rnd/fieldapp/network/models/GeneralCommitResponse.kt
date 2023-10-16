package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeneralCommitResponse(
    @SerializedName("data")
    val `data`: GeneralCommitData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class GeneralCommitData(
    @SerializedName("defaultCurrency")
    val defaultCurrency: String,
    @SerializedName("loanBalance")
    val loanBalance: String,
    @SerializedName("transactionCode")
    val transactionCode: String
)

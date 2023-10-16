package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PendingDisburseResponse(
    @SerializedName("data")
    val `data`: List<PendingDisburseData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class PendingDisburseData(
    @SerializedName("amountApplied")
    val amountApplied: Int,
    @SerializedName("amountApproved")
    val amountApproved: String,
    @SerializedName("applicationDate")
    val applicationDate: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("loanAccountNo")
    val loanAccountNo: String,
    @SerializedName("loanId")
    val loanId: Int,
    @SerializedName("name")
    val name: String
)

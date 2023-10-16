package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PendingApprovalResponse(
    @SerializedName("data")
    val `data`: List<PendingApprovalData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class PendingApprovalData(
    @SerializedName("amountApplied")
    val amountApplied: String,
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
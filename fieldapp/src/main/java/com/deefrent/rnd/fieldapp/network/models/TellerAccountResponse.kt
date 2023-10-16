package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TellerAccountResponse(
    @SerializedName("data")
    val `data`: TellerAccountData,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

@Keep
data class TellerAccountData(
    @SerializedName("balance")
    val balance: String, // 5430
    @SerializedName("loanRepayment")
    val loanRepayment: String, // 0
    @SerializedName("totalDisbursement")
    val totalDisbursement: String // 6077
)

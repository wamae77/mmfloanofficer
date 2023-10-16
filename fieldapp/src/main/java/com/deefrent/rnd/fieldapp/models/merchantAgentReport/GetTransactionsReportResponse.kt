package com.deefrent.rnd.fieldapp.models.merchantAgentReport

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GetTransactionsReportResponse(
    val Message: String,
    @SerializedName("data")
    @Expose
    val transactions: List<Transaction>,
    val status: String
)
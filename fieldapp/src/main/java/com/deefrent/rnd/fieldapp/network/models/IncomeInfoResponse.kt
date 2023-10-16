package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IncomeInfoResponse(
    @SerializedName("data")
    val `data`: IncomeInfoData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class IncomeInfoData(
    @SerializedName("other")
    val other: String,
    @SerializedName("otherBusinesses")
    val otherBusinesses: String,
    @SerializedName("ownSalary")
    val ownSalary: String,
    @SerializedName("remittanceOrDonation")
    val remittanceOrDonation: String,
    @SerializedName("rental")
    val rental: String,
    @SerializedName("roscals")
    val roscals: String
)

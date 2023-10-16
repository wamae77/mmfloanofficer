package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ExpensesInformationResponse(
    @SerializedName("data")
    val `data`: ExpensesInformationData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class ExpensesInformationData(
    @SerializedName("domesticWorkersWages")
    val domesticWorkersWages: String,
    @SerializedName("food")
    val food: String,
    @SerializedName("funeralPolicy")
    val funeralPolicy: String,
    @SerializedName("medicalAidOrContributions")
    val medicalAidOrContributions: String,
    @SerializedName("other")
    val other: String,
    @SerializedName("rentals")
    val rentals: String,
    @SerializedName("schoolFees")
    val schoolFees: String,
    @SerializedName("tithe")
    val tithe: String,
    @SerializedName("transport")
    val transport: String
)

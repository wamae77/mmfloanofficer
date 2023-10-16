package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccountLookUpResponse(
    @SerializedName("data")
    val `data`: LookUpData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class LookUpData(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("memberNumber")
    val memberNumber: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("website")
    val website: String
)

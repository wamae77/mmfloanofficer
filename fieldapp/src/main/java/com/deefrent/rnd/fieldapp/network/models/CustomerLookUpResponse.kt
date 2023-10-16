package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerLookUpResponse(
    @SerializedName("data")
    val `data`: CustomerLookUpData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class CustomerLookUpData(
    @SerializedName("maxCollaterals")
    val maxCollaterals: String,
    @SerializedName("maxGuarantors")
    val maxGuarantors: String,
    @SerializedName("minCollaterals")
    val minCollaterals: String,
    @SerializedName("minGuarantors")
    val minGuarantors: String,
    @SerializedName("registered")
    val registered: Boolean
)

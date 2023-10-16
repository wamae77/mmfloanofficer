package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HoouseHoldMembersResponse(
    @SerializedName("data")
    val `data`: List<HoouseHoldMembers>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class HoouseHoldMembers(
    val fullName: String,
    val relationShip: String,
    val relationshipId: String,
    val occupation: String,
    val occupationId: String,
    val natureOfActivity: String,
    val incomeOrFeesPaid: String,
)

package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.userDetails.UserDetails
import com.google.gson.annotations.SerializedName

@Keep
data class GetUserDetailsResponse(
    @SerializedName("data")
    val userDetails: UserDetails,
    val message: String,
    val status: String
)
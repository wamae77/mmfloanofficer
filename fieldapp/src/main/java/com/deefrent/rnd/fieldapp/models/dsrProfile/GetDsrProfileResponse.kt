package com.deefrent.rnd.fieldapp.models.dsrProfile

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GetDsrProfileResponse(
    @SerializedName("data")
    @Expose
    val dsrProfileData: DsrProfileData,
    val message: String,
    val status: String
)
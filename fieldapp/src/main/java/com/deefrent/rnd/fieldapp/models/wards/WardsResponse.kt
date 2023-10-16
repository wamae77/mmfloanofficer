package com.deefrent.rnd.fieldapp.models.wards

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WardsResponse(
    @SerializedName("data")
    @Expose
    val wardsData: WardsData,
    val message: String,
    val status: String
)
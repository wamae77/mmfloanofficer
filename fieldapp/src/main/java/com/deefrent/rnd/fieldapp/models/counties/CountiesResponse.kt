package com.deefrent.rnd.fieldapp.models.counties

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CountiesResponse(
    @SerializedName("data")
    @Expose
    val countiesData: CountiesData,
    val message: String,
    val status: String
)
package com.deefrent.rnd.fieldapp.models.complainTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ComplainTypesResponse(
    @SerializedName("data")
    @Expose
    val complainTypesData: ComplainTypesData,
    val message: String,
    val status: String
)
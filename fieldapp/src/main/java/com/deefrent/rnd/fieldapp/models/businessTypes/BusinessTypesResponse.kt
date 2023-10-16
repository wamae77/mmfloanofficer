package com.deefrent.rnd.fieldapp.models.businessTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BusinessTypesResponse(
    @SerializedName("data")
    @Expose
    val businessTypesData: BusinessTypesData,
    val message: String,
    val status: String
)
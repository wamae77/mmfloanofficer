package com.deefrent.rnd.fieldapp.models.liquidationTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LiquidationTypesResponse(
    @SerializedName("data")
    @Expose
    val liquidationTypeData: LiquidationTypeData,
    val message: String,
    val status: String
)
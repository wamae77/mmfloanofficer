package com.deefrent.rnd.fieldapp.models.liquidationTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LiquidationTypeData(
    @SerializedName("liquidationtype")
    @Expose
    val liquidationType: List<LiquidationType>
)
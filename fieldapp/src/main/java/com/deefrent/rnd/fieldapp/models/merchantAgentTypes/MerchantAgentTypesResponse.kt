package com.deefrent.rnd.fieldapp.models.merchantAgentTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class MerchantAgentTypesResponse(
    @SerializedName("data")
    @Expose
    val merchantAgentTypeData: MerchantAgentTypeData,
    val message: String,
    val status: String
)
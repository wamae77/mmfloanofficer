package com.deefrent.rnd.fieldapp.models.merchantAgentDetails

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class MerchantAgentDetailsData(
    @SerializedName("merchantdetails")
    @Expose
    val merchantDetails: MerchantDetails
)
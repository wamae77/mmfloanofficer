package com.deefrent.rnd.fieldapp.models.customerDetails

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerDetailsResponse(
    @SerializedName("data")
    @Expose
    val customerDetailsData: CustomerDetailsData,
    val status: String,
    val message:String
)
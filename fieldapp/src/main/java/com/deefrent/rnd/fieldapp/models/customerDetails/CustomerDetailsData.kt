package com.deefrent.rnd.fieldapp.models.customerDetails

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerDetailsData(
    @SerializedName("customerdetails")
    @Expose
    val customerDetails: CustomerDetails
)
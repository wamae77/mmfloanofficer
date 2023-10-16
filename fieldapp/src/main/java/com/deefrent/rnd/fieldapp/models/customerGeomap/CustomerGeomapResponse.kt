package com.deefrent.rnd.fieldapp.models.customerGeomap

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerGeomapResponse(
    @SerializedName("data")
    @Expose
    val customerGeomapData: CustomerGeomapData,
    val message: String,
    val status: String
)
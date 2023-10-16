package com.deefrent.rnd.fieldapp.models.customerGeomap

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerGeomapData(
    @SerializedName("merchantdetails")
    @Expose
    val customerGeomapDetails: CustomerGeomapDetails
)
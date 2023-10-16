package com.deefrent.rnd.fieldapp.models.customerDetails

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerKYC(
    val backIdCapture: String,
    val frontIdCapture: String,
    @SerializedName("passportPhotoCapture")
    @Expose
    val passportPhoto: String
)
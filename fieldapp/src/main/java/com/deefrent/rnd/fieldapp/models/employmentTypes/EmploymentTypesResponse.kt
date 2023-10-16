package com.deefrent.rnd.fieldapp.models.employmentTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class EmploymentTypesResponse(
    @SerializedName("data")
    @Expose
    val employmentTypesData: EmploymentTypesData,
    val message: String,
    val status: String
)
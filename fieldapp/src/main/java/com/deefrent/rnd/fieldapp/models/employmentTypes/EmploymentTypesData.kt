package com.deefrent.rnd.fieldapp.models.employmentTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class EmploymentTypesData(
    @SerializedName("employmenttypes")
    @Expose
    val employmentTypes: List<EmploymentType>
)
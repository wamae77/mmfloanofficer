package com.deefrent.rnd.fieldapp.models.complainTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ComplainTypesData(
    @SerializedName("complaintypes")
    @Expose
    val complainTypes: List<ComplainType>
)
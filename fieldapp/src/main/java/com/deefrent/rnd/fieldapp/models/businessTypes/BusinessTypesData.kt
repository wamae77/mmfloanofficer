package com.deefrent.rnd.fieldapp.models.businessTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BusinessTypesData(
    @SerializedName("businesstypes")
    @Expose
    val businessTypes: List<BusinessType>
)
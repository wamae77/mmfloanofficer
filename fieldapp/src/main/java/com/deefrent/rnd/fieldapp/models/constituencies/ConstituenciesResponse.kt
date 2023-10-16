package com.deefrent.rnd.fieldapp.models.constituencies

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ConstituenciesResponse(
    @SerializedName("data")
    @Expose
    val constituenciesData: ConstituenciesData,
    val message: String,
    val status: String
)
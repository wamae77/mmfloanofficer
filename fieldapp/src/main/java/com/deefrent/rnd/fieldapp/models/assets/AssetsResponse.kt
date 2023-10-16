package com.deefrent.rnd.fieldapp.models.assets

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class AssetsResponse(
    @SerializedName("data")
    @Expose
    val assetsData: AssetsData,
    val message: String,
    val status: String
)
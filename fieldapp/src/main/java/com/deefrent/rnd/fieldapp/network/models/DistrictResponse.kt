package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DistrictResponse(
    @SerializedName("data")
    val `data`: List<DistrictData>,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
) {
    data class DistrictData(
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String // Bulawayo Province
    )
}
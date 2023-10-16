package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VillagesResponse(
    @SerializedName("data")
    val `data`: List<VillagesData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

data class VillagesData(
    @SerializedName("district_id")
    val districtId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

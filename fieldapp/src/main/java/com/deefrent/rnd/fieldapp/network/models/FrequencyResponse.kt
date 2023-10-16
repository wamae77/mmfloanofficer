package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FrequencyResponse(
    @SerializedName("data")
    val `data`: List<FrequencyData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class FrequencyData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String
) {
    override fun toString(): String {
        return label
    }
}

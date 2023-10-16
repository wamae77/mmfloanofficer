package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GenderResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val `data`: List<GenderItems>
)

@Keep
data class GenderItems(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String//Female

) {
    override fun toString(): String {
        return name
    }
}

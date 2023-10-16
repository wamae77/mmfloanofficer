package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SetSecurityQuizResponse(
    @SerializedName("data")
    val `data`: List<SetSecurityQuizData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class SetSecurityQuizData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

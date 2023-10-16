package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EmploymentResponse(
    @SerializedName("data")
    val `data`: List<EmploymentData>,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

@Keep
data class EmploymentData(
    @SerializedName("id")
    val id: Int, // 1
    @SerializedName("name")
    val name: String // Employee
) {
    override fun toString(): String {
        return name
    }
}

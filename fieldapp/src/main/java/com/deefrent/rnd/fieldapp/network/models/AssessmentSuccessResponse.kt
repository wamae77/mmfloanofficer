package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AssessmentSuccessResponse(
    @SerializedName("data")
    val `data`: AssessmentSuccessData,
    @SerializedName("message")
    val message: String, // Saved successfully
    @SerializedName("status")
    val status: Int // 1
)

@Keep
data class AssessmentSuccessData(
    @SerializedName("idNumber")
    val idNumber: String, // tyy
    @SerializedName("loanPurposes")
    val loanPurposes: List<LoanPurpose>
)

@Keep
data class LoanPurpose(
    @SerializedName("id")
    val id: Int, // 1
    @SerializedName("name")
    val name: String // Emergency
)

package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserRegistrationResponse(
    @SerializedName("data")
    val `data`: UserRegistrationData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class UserRegistrationData(
    @SerializedName("facePhotoUrl")
    val facePhotoUrl: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("idBackUrl")
    val idBackUrl: String,
    @SerializedName("idFrontUrl")
    val idFrontUrl: String,
    @SerializedName("memberNumber")
    val memberNumber: String,
    @SerializedName("passportPhotoUrl")
    val passportPhotoUrl: String,
    @SerializedName("username")
    val username: String
)

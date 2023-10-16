package com.deefrent.rnd.fieldapp.models.userTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class UserTypeResponse(
    @SerializedName("data")
    @Expose
    val userTypeData: UserTypeData,
    val message: String,
    val status: String
)
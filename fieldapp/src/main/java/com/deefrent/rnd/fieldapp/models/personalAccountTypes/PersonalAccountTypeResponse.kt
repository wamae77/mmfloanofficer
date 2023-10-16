package com.deefrent.rnd.fieldapp.models.personalAccountTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class PersonalAccountTypeResponse(
    @SerializedName("data")
    @Expose
    val personalAccountTypeData: PersonalAccountTypeData,
    val message: String,
    val status: String
)
package com.deefrent.rnd.fieldapp.models.idType

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GetIDTypeResponse(
    @SerializedName("data")
    @Expose
    val getIDTypeData: GetIDTypeData,
    val message: String,
    val status: String
)
package com.deefrent.rnd.fieldapp.models.xaraniIdCheck.request


import com.google.gson.annotations.SerializedName

data class XaraniIdCheckRequest(
    @SerializedName("idNumber")
    val idNumber: String?
)
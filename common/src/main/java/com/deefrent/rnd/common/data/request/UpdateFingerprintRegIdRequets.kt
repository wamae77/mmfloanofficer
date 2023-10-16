package com.deefrent.rnd.common.data.request


import com.google.gson.annotations.SerializedName

data class UpdateFingerprintRegIdRequets(
    @SerializedName("clientId")
    val clientId: Int?,
    @SerializedName("fingerPrintRegId")
    val fingerPrintRegId: String?
)
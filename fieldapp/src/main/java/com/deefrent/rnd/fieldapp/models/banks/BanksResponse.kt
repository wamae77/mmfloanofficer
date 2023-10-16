package com.deefrent.rnd.fieldapp.models.banks

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BanksResponse(
    @SerializedName("data")
    @Expose
    val banksData: BanksData,
    val message: String,
    val status: String
)
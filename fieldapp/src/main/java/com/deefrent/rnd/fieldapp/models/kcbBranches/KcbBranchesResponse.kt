package com.deefrent.rnd.fieldapp.models.kcbBranches

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class KcbBranchesResponse(
    @SerializedName("data")
    @Expose
    val kcbBranchesData: KcbBranchesData,
    val message: String,
    val status: String
)
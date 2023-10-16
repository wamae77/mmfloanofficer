package com.deefrent.rnd.fieldapp.models.bankBranches

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BankBranchesResponse(
    @SerializedName("data")
    @Expose
    val bankBranchesData: BankBranchesData,
    val message: String,
    val status: String
)
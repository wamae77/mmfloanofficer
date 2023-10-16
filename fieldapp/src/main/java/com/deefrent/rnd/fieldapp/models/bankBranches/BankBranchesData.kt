package com.deefrent.rnd.fieldapp.models.bankBranches

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BankBranchesData(
    @SerializedName("bankbranches")
    @Expose
    val bankBranches: List<BankBranch>
)
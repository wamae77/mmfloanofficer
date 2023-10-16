package com.deefrent.rnd.fieldapp.models.kcbBranches

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class KcbBranchesData(
    @SerializedName("kcbbranches")
    @Expose
    val kcbBranches: List<KcbBranch>
)
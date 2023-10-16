package com.deefrent.rnd.fieldapp.models.kcbBranches

import androidx.annotation.Keep

@Keep
data class KcbBranch(
    val branchCode: Int,
    val branchName: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return branchName
    }
}
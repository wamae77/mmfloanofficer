package com.deefrent.rnd.fieldapp.models.bankBranches

import androidx.annotation.Keep

@Keep
data class BankBranch(
    val bankCode: String,
    val branchCode: String,
    val branchName: String,
    val createdBy: Int,
    val createdOn: String,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return branchName
    }
}
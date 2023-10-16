package com.deefrent.rnd.fieldapp.models.banks

import androidx.annotation.Keep

@Keep
data class Bank(
    val bankCode: String,
    val bankName: String,
    val createdBy: Int,
    val createdOn: String,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return bankName
    }
}
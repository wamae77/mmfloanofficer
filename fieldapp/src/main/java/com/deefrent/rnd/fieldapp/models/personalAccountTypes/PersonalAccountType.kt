package com.deefrent.rnd.fieldapp.models.personalAccountTypes

import androidx.annotation.Keep

@Keep
data class PersonalAccountType(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val personalAccountTypeName: String,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return personalAccountTypeName
    }
}
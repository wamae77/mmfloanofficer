package com.deefrent.rnd.fieldapp.models.userTypes

import androidx.annotation.Keep

@Keep
data class UserType(
    val UserAccountTypeName: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return UserAccountTypeName
    }
}
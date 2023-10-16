package com.deefrent.rnd.fieldapp.models.businessTypes

import androidx.annotation.Keep

@Keep
data class BusinessType(
    val businessTypeName: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return businessTypeName
    }
}
package com.deefrent.rnd.fieldapp.models.wards

import androidx.annotation.Keep

@Keep
data class Ward(
    val constituencyCode: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String,
    val wardCode: Int,
    val wardName: String
){
    override fun toString(): String {
        return wardName
    }
}
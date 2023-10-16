package com.deefrent.rnd.fieldapp.models.complainTypes

import androidx.annotation.Keep

@Keep
data class ComplainType(
    val complainType: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedBy: Int,
    val updatedOn: String
){
    override fun toString(): String {
        return complainType
    }
}
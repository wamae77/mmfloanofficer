package com.deefrent.rnd.fieldapp.models.employmentTypes

import androidx.annotation.Keep

@Keep
data class EmploymentType(
    val createdBy: Int,
    val createdOn: String,
    val employmentTypeName: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return employmentTypeName
    }
}
package com.deefrent.rnd.fieldapp.models.counties

import androidx.annotation.Keep

@Keep
data class County(
    val countyCode: Int,
    val countyName: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return countyName
    }
}
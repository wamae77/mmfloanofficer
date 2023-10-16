package com.deefrent.rnd.fieldapp.models.constituencies

import androidx.annotation.Keep

@Keep
data class Constituency(
    val constituencyCode: Int,
    val constituencyName: String,
    val countyCode: Int,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return constituencyName
    }
}
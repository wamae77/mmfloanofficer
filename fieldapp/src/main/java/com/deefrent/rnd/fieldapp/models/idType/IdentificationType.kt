package com.deefrent.rnd.fieldapp.models.idType

import androidx.annotation.Keep

@Keep
data class IdentificationType(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}
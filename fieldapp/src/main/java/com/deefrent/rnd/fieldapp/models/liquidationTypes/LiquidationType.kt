package com.deefrent.rnd.fieldapp.models.liquidationTypes

import androidx.annotation.Keep

@Keep
data class LiquidationType(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val liquidationType: String,
    val status: String,
    val updatedOn: String
){
    override fun toString(): String {
        return liquidationType
    }
}
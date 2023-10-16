package com.deefrent.rnd.fieldapp.models.assets

import androidx.annotation.Keep

@Keep
data class Asset(
    val assetDesc: String,
    val assetName: String,
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val updatedBy: Int,
    val updatedOn: String
){
    override fun toString(): String {
        return assetName
    }
}
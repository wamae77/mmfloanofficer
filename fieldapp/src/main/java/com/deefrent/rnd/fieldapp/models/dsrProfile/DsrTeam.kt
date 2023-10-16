package com.deefrent.rnd.fieldapp.models.dsrProfile

import androidx.annotation.Keep

@Keep
data class DsrTeam(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: String,
    val teamLocation: String,
    val teamName: String,
    val updatedOn: String
)
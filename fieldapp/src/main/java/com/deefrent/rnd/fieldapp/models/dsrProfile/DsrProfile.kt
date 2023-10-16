package com.deefrent.rnd.fieldapp.models.dsrProfile

import androidx.annotation.Keep

@Keep
data class DsrProfile(
    val createdBy: Int,
    val createdOn: String,
    val dsrTeam: DsrTeam,
    val email: String,
    val firstName: String,
    val gender: String,
    val id: Int,
    val idNumber: String,
    val lastName: String,
    val location: String,
    val mobileNo: String,
    val staffNo: String,
    val status: String,
    val systemUserId: Int,
    val username: String
)
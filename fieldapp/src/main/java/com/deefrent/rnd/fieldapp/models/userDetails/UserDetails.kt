package com.deefrent.rnd.fieldapp.models.userDetails

import androidx.annotation.Keep

@Keep
data class UserDetails(
    val email: String,
    val guiId: String,
    val mobileNo: String,
    val username: String
)
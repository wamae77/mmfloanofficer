package com.deefrent.rnd.fieldapp.models.loans

import androidx.annotation.Keep

@Keep
data class MemberInformation(
    val from: String,
    val fullName: String,
    val memberNo: String,
    val nationalId: String,
    val phone: String,
    val to: String
)
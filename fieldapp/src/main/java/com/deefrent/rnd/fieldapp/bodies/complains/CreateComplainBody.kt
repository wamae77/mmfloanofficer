package com.deefrent.rnd.fieldapp.bodies.complains

data class CreateComplainBody(
    val accountNo: String,
    val complainTypeId: Int,
    val message: String,
    val subject: String,
    val userAccountTypeId: Int
)
package com.deefrent.rnd.fieldapp.bodies.assetManagement

data class SubmitAssetBody(
    val accountNo: String,
    val assetId: Int,
    val userAccountType: Int
)
package com.deefrent.rnd.fieldapp.models.merchantAgentDetails

import androidx.annotation.Keep

@Keep
data class MerchantDetails(
    val accountId: String,
    val accountName: String,
    val accountNumber: String,
    val bankCode: String,
    val bankName: String,
    val branchCode: String,
    val branchName: String,
    val buildingName: String,
    val businessName: String,
    val businessPermit: String,
    val businessTypeName: String,
    val companyRegistration: String,
    val constituencyCode: Int,
    val constituencyName: String,
    val countyCode: Int,
    val countyName: String,
    val customerPhoto: String,
    val email: String,
    val fkdPhoto: String,
    val id: Int,
    val latitude: String,
    val liquidationRate: Double,
    val liquidationType: String,
    val longitude: String,
    val merchAgentAccountTypeName: String,
    val natureBusiness: String,
    val roomNumber: String,
    val signaturePhoto: String,
    val streetName: String,
    val phoneNo: String,
    val termsAndConditionsDoc: String,
    val town: String,
    val wardCode: Int,
    val wardName: String,
    val merchantKYCList: List<MerchantAgentKYC>
)
package com.deefrent.rnd.fieldapp.models.merchantAgentDetails

import androidx.annotation.Keep

@Keep
data class MerchantAgentKYC(
    val businessLicense: String,
    val signatureDocDoc: String,
    val backID: String,
    val termsAndConditionDoc: String,
    val businessPermitDoc: String,
    val certificateOFGoodConduct: String,
    val companyRegistrationDoc: String,
    val frontID: String,
    val customerPhoto: String,
    val kraPinCertificate: String,
    val shopPhoto: String,
    val fieldApplicationForm: String,
)

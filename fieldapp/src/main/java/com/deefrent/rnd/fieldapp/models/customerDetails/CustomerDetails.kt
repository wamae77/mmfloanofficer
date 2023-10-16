package com.deefrent.rnd.fieldapp.models.customerDetails

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.employmentTypes.EmploymentType

@Keep
data class CustomerDetails(
    val accountOpeningPurporse: String,
    val backIdCapture: String,
    val companyYouWorkFor: String,
    val createdBy: Int,
    val createdOn: String,
    val crmAccountNo: Int,
    val crmLeadId: String,
    val dob: String,
    val employmentType: EmploymentType,
    val firstName: String,
    val frontIdCapture: String,
    val gender: String,
    val id: Int,
    val income: Double,
    val lastName: String,
    val latitude: String,
    val longitude: String,
    val passportCapture: String,
    val phoneNo: String,
    val sur_name: String,
    val accountTypeName: String,
    val customerKYCList: List<CustomerKYC>
)
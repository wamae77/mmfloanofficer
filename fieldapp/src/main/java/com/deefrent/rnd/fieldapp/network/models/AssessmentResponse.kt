package com.deefrent.rnd.fieldapp.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class AssessmentResponse(
    @SerializedName("data")
    val `data`: IncompleteDataItem,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class IncompleteDataItem(
    @SerializedName("incompleteItems")
    val customerAssData: List<CustomerAssessmentData>,
    @SerializedName("maxCollaterals")
    val maxCollaterals: String,
    @SerializedName("maxGuarantors")
    val maxGuarantors: String,
    @SerializedName("maxHouseholdMembers")
    val maxHouseholdMembers: String,
    @SerializedName("minCollaterals")
    val minCollaterals: String,
    @SerializedName("minGuarantors")
    val minGuarantors: String
)

class CustomerAssessmentData(
    @SerializedName("alsoKnownAs")
    val alsoKnownAs: String,
    @SerializedName("assessmentPercentage")
    val assessmentPercentage: String, // 40.00
    @SerializedName("assessmentRemarks")
    val assessmentRemarks: String,
    @SerializedName("businessDistrictId")
    val businessDistrictId: String, // null
    @SerializedName("businessDistrictName")
    val businessDistrictName: String,
    @SerializedName("businessPhone")
    val businessPhone: String,
    @SerializedName("businessPhysicalAddress")
    val businessPhysicalAddress: String,
    @SerializedName("businessTypeId")
    val businessTypeId: String, // null
    @SerializedName("businessTypeName")
    val businessTypeName: String,
    @SerializedName("businessVillageId")
    val businessVillageId: String, // null
    @SerializedName("businessVillageName")
    val businessVillageName: String,
    @SerializedName("customerNumber")
    val customerNumber: String, // 261
    @SerializedName("dob")
    val dob: String, // 1994-12-12
    @SerializedName("economicFactorId")
    val economicFactorId: String, // null
    @SerializedName("economicFactorName")
    val economicFactorName: String,
    @SerializedName("educationLevel")
    val educationLevel: String,
    @SerializedName("educationLevelId")
    val educationLevelId: String, // null
    @SerializedName("email")
    val email: String, // mary@gmail.com
    @SerializedName("empStatus")
    val empStatus: String, // Independent Professional
    @SerializedName("empStatusId")
    val empStatusId: String, // null
    @SerializedName("establishmentTypeId")
    val establishmentTypeId: String, // null
    @SerializedName("establishmentTypeName")
    val establishmentTypeName: String,
    @SerializedName("expenseDomesticWorkersWages")
    val expenseDomesticWorkersWages: String,
    @SerializedName("expenseFood")
    val expenseFood: String,
    @SerializedName("expenseFuneralPolicy")
    val expenseFuneralPolicy: String,
    @SerializedName("expenseMedicalAidOrContributions")
    val expenseMedicalAidOrContributions: String,
    @SerializedName("expenseRentals")
    val expenseRentals: String,
    @SerializedName("expenseSchoolFees")
    val expenseSchoolFees: String,
    @SerializedName("expenseTithe")
    val expenseTithe: String,
    @SerializedName("expenseTransport")
    val expenseTransport: String,
    @SerializedName("firstName")
    val firstName: String, // Mary
    @SerializedName("gender")
    val gender: String, // Female
    @SerializedName("genderId")
    val genderId: String, // 38
    @SerializedName("idNumber")
    val idNumber: String, // 1234567890u
    @SerializedName("identifier")
    val identifier: String, // Recommendation
    @SerializedName("identifierId")
    val identifierId: String, // null
    @SerializedName("incomeNetSalary")
    val incomeNetSalary: String,
    @SerializedName("incomeOwnSalary")
    val incomeOwnSalary: String,
    @SerializedName("incomeRemittanceOrDonation")
    val incomeRemittanceOrDonation: String,
    @SerializedName("incomeRental")
    val incomeRental: String,
    @SerializedName("incomeTotalSales")
    val incomeTotalSales: String,
    @SerializedName("incomeProfit")
    val incomeProfit: String, // false
    @SerializedName("lastName")
    val lastName: String, // Luths
    @SerializedName("nameOfIndustry")
    val nameOfIndustry: String,
    @SerializedName("numberOfChildren")
    val numberOfChildren: String, // 5
    @SerializedName("numberOfDependants")
    val numberOfDependants: String, // 2
    @SerializedName("numberOfEmployees")
    val numberOfEmployees: String,
    @SerializedName("otherExpenses")
    val otherExpenses: String,
    @SerializedName("otherIncomes")
    val otherIncomes: String,
    @SerializedName("phone")
    val phone: String, // 0700000000
    @SerializedName("spouseName")
    val spouseName: String,
    @SerializedName("spousePhone")
    val spousePhone: String,
    @SerializedName("yearsInBusiness")
    val yearsInBusiness: String, // null
    @SerializedName("resAccommodationStatusId")
    val resAccommodationStatusId: String, // null
    @SerializedName("resAccommodationStatusName")
    val resAccommodationStatusName: String, // Owned
    @SerializedName("resAddress")
    val resAddress: String, // fff
    @SerializedName("kinFirstName")
    val kinFirstName: String, // ffff
    @SerializedName("kinIdentityNumber")
    val kinIdentityNumber: String, // dddddddddddd
    @SerializedName("kinIdentityType")
    val kinIdentityType: String, // Passport No.
    @SerializedName("kinIdentityTypeId")
    val kinIdentityTypeId: String, // null
    @SerializedName("kinLastName")
    val kinLastName: String, // 0788547777
    @SerializedName("kinPhone")
    val kinPhone: String, // 0788547777
    @SerializedName("kinRelationship")
    val kinRelationship: String, // Spouse
    @SerializedName("kinRelationshipId")
    val kinRelationshipId: String, // null
    @SerializedName("resLivingSince")
    val resLivingSince: String, // 2022-03-01
    @SerializedName("collateralInfo")
    val collateralInfo: List<CustomerCollateralInfo>,
    @SerializedName("guarantorInfo")
    val guarantorInfo: List<CustomerGuarantorInfo>,
    @SerializedName("otherBorrowings")
    val otherBorrowings: List<CustomerOtherBorrowing>,
    @SerializedName("householdMembers")
    val householdMembers: List<CustomerHouseholdMember>,
    @SerializedName("documents")
    val documents: List<CustomerDocuments>,
    @SerializedName("customerId")
    val customerId: Int,
    @SerializedName("areaName")
    val areaName: String,
    @SerializedName("areaId")
    val areaId: String
)

@Keep
data class CustomerDocuments(
    @SerializedName("channelGeneratedCode")
    val channelGeneratedCode: String, // 123456
    @SerializedName("description")
    val description: String,
    @SerializedName("docTypeCode")
    val docTypeCode: String, // PROFILE-DOC
    @SerializedName("docTypeId")
    val docTypeId: Int, // 2
    @SerializedName("docTypeName")
    val docTypeName: String, // Passport Size Photo
    @SerializedName("documentId")
    val documentId: Int, // 1
    @SerializedName("url")
    val url: String // https://test-api.ekenya.co.ke/moneymart-api/uploads/organizations/013/clients/MOM110/documents/PassportSizePhoto.jpg
)

@Keep
data class CustomerCollateralInfo(
    @SerializedName("assetTypeId")
    val assetTypeId: String,
    @SerializedName("assetTypeName")
    val assetTypeName: String,
    @SerializedName("estimatedValue")
    val estimatedValue: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("serialNumber")
    val serialNumber: String,
    @SerializedName("channelGeneratedCode")
    val channelGeneratedCode: String,
)

@Keep
data class CustomerGuarantorInfo(
    @SerializedName("address")
    val address: String,
    @SerializedName("guarantorId")
    val guarantorId: Int,
    @SerializedName("idNumber")
    val idNumber: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("relationshipId")
    val relationshipId: Int,
    @SerializedName("relationshipName")
    val relationshipName: String,
    @SerializedName("channelGeneratedCode")
    val channelGeneratedCode: String,
)

@Parcelize
data class CustomerHouseholdMember(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("incomeOrFeesPaid")
    val incomeOrFeesPaid: String,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("natureOfActivity")
    val natureOfActivity: String,
    @SerializedName("occupation")
    val occupation: String,
    @SerializedName("occupationId")
    val occupationId: String,
    @SerializedName("relationShip")
    val relationShip: String,
    @SerializedName("relationshipId")
    val relationshipId: String
) : Parcelable

data class CustomerOtherBorrowing(
    @SerializedName("amount")
    val amount: String, // 1000.0000
    @SerializedName("amountPaidToDate")
    val amountPaidToDate: String, // 100.0000
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("institutionName")
    val institutionName: String, // Safaricom
    @SerializedName("monthlyInstallmentPaid")
    val monthlyInstallmentPaid: String, // 50.0000
    @SerializedName("otherBorrowingId")
    val otherBorrowingId: Int, // 37
    @SerializedName("status")
    val status: String, // Active
    @SerializedName("statusId")
    val statusId: Int // 1
)
    

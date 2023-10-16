package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IncompleteRegResponse(
    @SerializedName("data")
    val `data`: RegData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class RegData(
    @SerializedName("incompleteItems")
    val incompleteItems: List<CustomerIncompleteData>,
    @SerializedName("maxCollaterals")
    val maxCollaterals: String, // 3
    @SerializedName("maxGuarantors")
    val maxGuarantors: String, // 3
    @SerializedName("maxHouseholdMembers")
    val maxHouseholdMembers: String,
    @SerializedName("minCollaterals")
    val minCollaterals: String, // 1
    @SerializedName("minGuarantors")
    val minGuarantors: String // 1
)

@Keep
data class CustomerIncompleteData(
    @SerializedName("alsoKnownAs")
    val alsoKnownAs: String,
    @SerializedName("businessDistrictId")
    val businessDistrictId: String,
    @SerializedName("businessDistrictName")
    val businessDistrictName: String,
    @SerializedName("businessPhone")
    val businessPhone: String,
    @SerializedName("businessPhysicalAddress")
    val businessPhysicalAddress: String,
    @SerializedName("businessTypeId")
    val businessTypeId: String,
    @SerializedName("businessTypeName")
    val businessTypeName: String,
    @SerializedName("businessVillageId")
    val businessVillageId: String,
    @SerializedName("businessVillageName")
    val businessVillageName: String,
    @SerializedName("channel")
    val channel: String, // USSD
    @SerializedName("customerNumber")
    val customerNumber: String, // 254
    @SerializedName("dateRecorded")
    val dateRecorded: String, // 2022-03-19 13:09:15
    @SerializedName("dob")
    val dob: String, // 1994-12-12
    @SerializedName("economicFactorId")
    val economicFactorId: String,
    @SerializedName("economicFactorName")
    val economicFactorName: String,
    @SerializedName("educationLevel")
    val educationLevel: String,
    @SerializedName("educationLevelId")
    val educationLevelId: String,
    @SerializedName("email")
    val email: String, // ontt13333@gmail.com
    @SerializedName("empStatus")
    val empStatus: String,
    @SerializedName("empStatusId")
    val empStatusId: String,
    @SerializedName("establishmentTypeId")
    val establishmentTypeId: String,
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
    val firstName: String, // muhia2
    @SerializedName("fullName")
    val fullName: String, // muhia2 muhia2
    @SerializedName("gender")
    val gender: String, // Female
    @SerializedName("genderId")
    val genderId: String, // 38
    @SerializedName("id")
    val id: Int, // 409
    @SerializedName("idNumber")
    val idNumber: String, // 69333331195699
    @SerializedName("identifier")
    val identifier: String,
    @SerializedName("identifierId")
    val identifierId: String,
    @SerializedName("incomeNetSalary")
    val incomeNetSalary: String,
    @SerializedName("incomeOtherBusinesses")
    val incomeOtherBusinesses: String,
    @SerializedName("incomeOwnSalary")
    val incomeOwnSalary: String,
    @SerializedName("incomeProfit")
    val incomeProfit: String,
    @SerializedName("incomeRemittanceOrDonation")
    val incomeRemittanceOrDonation: String,
    @SerializedName("incomeRental")
    val incomeRental: String,
    @SerializedName("incomeRoscals")
    val incomeRoscals: String,
    @SerializedName("incomeTotalSales")
    val incomeTotalSales: String,
    @SerializedName("isCompletion")
    val isCompletion: String, // 1
    @SerializedName("isFullyRegistered")
    val isFullyRegistered: Boolean, // false
    @SerializedName("kinFirstName")
    val kinFirstName: String,
    @SerializedName("kinIdentityNumber")
    val kinIdentityNumber: String,
    @SerializedName("kinIdentityType")
    val kinIdentityType: String,
    @SerializedName("kinIdentityTypeId")
    val kinIdentityTypeId: String,
    @SerializedName("kinLastName")
    val kinLastName: String,
    @SerializedName("kinPhone")
    val kinPhone: String,
    @SerializedName("kinRelationship")
    val kinRelationship: String,
    @SerializedName("kinRelationshipId")
    val kinRelationshipId: String,
    @SerializedName("lastName")
    val lastName: String, // muhia2
    @SerializedName("nameOfIndustry")
    val nameOfIndustry: String,
    @SerializedName("numberOfChildren")
    val numberOfChildren: String,
    @SerializedName("numberOfDependants")
    val numberOfDependants: String,
    @SerializedName("numberOfEmployees")
    val numberOfEmployees: String,
    @SerializedName("otherExpenses")
    val otherExpenses: String,
    @SerializedName("otherIncomes")
    val otherIncomes: String,
    @SerializedName("phone")
    val phone: String, // 0789300900
    @SerializedName("resAccommodationStatusId")
    val resAccommodationStatusId: String,
    @SerializedName("resAccommodationStatusName")
    val resAccommodationStatusName: String,
    @SerializedName("resAddress")
    val resAddress: String,
    @SerializedName("resLivingSince")
    val resLivingSince: String,
    @SerializedName("spouseName")
    val spouseName: String, // Ojuka Calvince
    @SerializedName("spousePhone")
    val spousePhone: String, // 073678790
    @SerializedName("status")
    val status: String, // Pending Completion
    @SerializedName("yearsInBusiness")
    val yearsInBusiness: String,
    @SerializedName("otherBorrowings")
    val otherBorrowings: List<OBorrowing>,
    @SerializedName("guarantorInfo")
    val guarantorInfo: List<GuaInfo>,
    @SerializedName("householdMembers")
    val householdMembers: List<HMember>,
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("collateralInfo")
    val collateralInfo: List<CollInfo>,
)

data class CollInfo(
    @SerializedName("assetTypeId")
    val assetTypeId: Int, // 3
    @SerializedName("assetTypeName")
    val assetTypeName: String, // Household Assets
    @SerializedName("channelGeneratedCode")
    val channelGeneratedCode: String,
    @SerializedName("collateralId")
    val collateralId: Int, // 158
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("estimatedValue")
    val estimatedValue: String, // 400
    @SerializedName("model")
    val model: String, // samsung
    @SerializedName("name")
    val name: String, // Plasma TV
    @SerializedName("serialNumber")
    val serialNumber: String // 15tfa5
)

data class Document(
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

data class GuaInfo(
    @SerializedName("address")
    val address: String, // 03 Tankatara Norton
    @SerializedName("channelGeneratedCode")
    val channelGeneratedCode: String,
    @SerializedName("guarantorId")
    val guarantorId: Int, // 406
    @SerializedName("idNumber")
    val idNumber: String, // 17-157288H17
    @SerializedName("name")
    val name: String, // Misheck Makaza
    @SerializedName("phone")
    val phone: String, // 0775243643
    @SerializedName("relationshipId")
    val relationshipId: Int, // 139
    @SerializedName("relationshipName")
    val relationshipName: String // Sibling
)

data class HMember(
    @SerializedName("fullName")
    val fullName: String, // Linda Mudavanhu
    @SerializedName("incomeOrFeesPaid")
    val incomeOrFeesPaid: String, // 0.0000
    @SerializedName("memberId")
    val memberId: String, // 93
    @SerializedName("natureOfActivity")
    val natureOfActivity: String, // n/a
    @SerializedName("occupation")
    val occupation: String, // Student
    @SerializedName("occupationId")
    val occupationId: String, // 1
    @SerializedName("relationShip")
    val relationShip: String, // Daughter
    @SerializedName("relationshipId")
    val relationshipId: String // 137
)

data class OBorrowing(
    @SerializedName("amount")
    val amount: String, // 50000.0000
    @SerializedName("amountPaidToDate")
    val amountPaidToDate: String, // 49200.0000
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("institutionName")
    val institutionName: String, // CBZ BANK
    @SerializedName("monthlyInstallmentPaid")
    val monthlyInstallmentPaid: String, // 450.0000
    @SerializedName("otherBorrowingId")
    val otherBorrowingId: Int, // 36
    @SerializedName("status")
    val status: String, // Active
    @SerializedName("statusId")
    val statusId: Int // 1
)

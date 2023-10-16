package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FullCustomerDetailsResponse(
    @SerializedName("data")
    val `data`: FullCustomerDetailsData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
    data class FullCustomerDetailsData(
        @SerializedName("basicInfo")
        val basicInfo: BasicInfo,
        @SerializedName("businessAddress")
        val businessAddress: BusinessAddress,
        @SerializedName("businessDetails")
        val businessDetails: BusinessDetails,
        @SerializedName("collateralInfo")
        val collateralInfo: List<CollateralInfo>,
        @SerializedName("guarantorInfo")
        val guarantorInfo: List<GuarantorInfo>,
        @SerializedName("kinInfo")
        val kinInfo: KinInfo,
        @SerializedName("otherBorrowings")
        val otherBorrowings: List<OtherBorrowing>,
        @SerializedName("residenceInfo")
        val residenceInfo: ResidenceInfo,
        @SerializedName("residenceDoc")
        val residenceDoc:ResidenceDoc,
        @SerializedName("businessDoc")
        val businessDoc: BusinessDoc,
        @SerializedName("educationDoc")
        val educationDoc: EducationDoc,
    )
    data class BasicInfo(
        @SerializedName("channel")
        val channel: String,
        @SerializedName("dateRecorded")
        val dateRecorded: String,
        @SerializedName("dob")
        val dob: String,
        @SerializedName("educationLevel")
        val educationLevel: String,
        @SerializedName("educationLevelId")
        val educationLevelId: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("empStatus")
        val empStatus: String,
        @SerializedName("empStatusId")
        val empStatusId: String,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("fullName")
        val fullName: String,
        @SerializedName("gender")
        val gender: String,
        @SerializedName("genderId")
        val genderId: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("idNumber")
        val idNumber: String,
        @SerializedName("identifier")
        val identifier: String,
        @SerializedName("identifierId")
        val identifierId: String,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("numberOfChildren")
        val numberOfChildren: String,
        @SerializedName("numberOfDependants")
        val numberOfDependants: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("spouseName")
        val spouseName: String,
        @SerializedName("spousePhone")
        val spousePhone: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("passportDoc")
        val passportDoc: PassportDoc,
        @SerializedName("frontIdDoc")
        val frontIdDoc: FrontIdDoc
    )
    data class FrontIdDoc(
        @SerializedName("code")
        val code: String, // FRONT-ID-DOC
        @SerializedName("documentId")
        val documentId: Int, // 77
        @SerializedName("url")
        val url: String // https://test-portal.ekenya.co.ke/moneymart-tijara/uploads/organizations/013/clients/MN226S/id-front-photo.jpg
    )
    data class ResidenceDoc(
        @SerializedName("code")
        val code: String, // RESIDENCE-DOC
        @SerializedName("documentId")
        val documentId: Int, // 78
        @SerializedName("url")
        val url: String // https://test-portal.ekenya.co.ke/moneymart-tijara/uploads/organizations/013/clients/MN226S/proof-of-residence-doc.jpg
    )
    data class PassportDoc(
        @SerializedName("code")
        val code: String, // PROFILE-DOC
        @SerializedName("documentId")
        val documentId: Int, // 76
        @SerializedName("url")
        val url: String // https://test-portal.ekenya.co.ke/moneymart-tijara/uploads/organizations/013/clients/MN226S/passport-photo.jpg
    )
    data class BusinessDoc(
        @SerializedName("code")
        val code: String, // PROFILE-DOC
        @SerializedName("documentId")
        val documentId: Int, // 76
        @SerializedName("url")
        val url: String // https://test-portal.ekenya.co.ke/moneymart-tijara/uploads/organizations/013/clients/MN226S/passport-photo.jpg
    )
data class EducationDoc(
        @SerializedName("code")
        val code: String, // PROFILE-DOC
        @SerializedName("documentId")
        val documentId: Int, // 76
        @SerializedName("url")
        val url: String // https://test-portal.ekenya.co.ke/moneymart-tijara/uploads/organizations/013/clients/MN226S/passport-photo.jpg
    )

    data class BusinessAddress(
        @SerializedName("district")
        val district: String,
        @SerializedName("districtId")
        val districtId: String,
        @SerializedName("numberOfEmployees")
        val numberOfEmployees: String,
        @SerializedName("phoneNumber")
        val phoneNumber: String,
        @SerializedName("physicalAddress")
        val physicalAddress: String,
        @SerializedName("village")
        val village: String,
        @SerializedName("villageId")
        val villageId: String
    )

    data class BusinessDetails(
        @SerializedName("economicSector")
        val economicSector: String,
        @SerializedName("economicSectorId")
        val economicSectorId: String,
        @SerializedName("establishmentType")
        val establishmentType: String,
        @SerializedName("establishmentTypeId")
        val establishmentTypeId: String,
        @SerializedName("nameOfIndustry")
        val nameOfIndustry: String,
        @SerializedName("typeOfBusiness")
        val typeOfBusiness: String,
        @SerializedName("typeOfBusinessId")
        val typeOfBusinessId: String,
        @SerializedName("yearsInBusiness")
        val yearsInBusiness: String
    )

    data class CollateralInfo(
        @SerializedName("assetTypeId")
        val assetTypeId: String,
        @SerializedName("assetTypeName")
        val assetTypeName: String,
        @SerializedName("collateralId")
        val collateralId: String,
        @SerializedName("estimatedValue")
        val estimatedValue: String,
        @SerializedName("model")
        val model: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("document")
        val document: CollateralDocument,
        @SerializedName("serialNumber")
        val serialNumber: String,
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String
    )

    data class GuarantorInfo(
        @SerializedName("address")
        val address: String,
        @SerializedName("guarantorId")
        val guarantorId: String,
        @SerializedName("idNumber")
        val idNumber: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("relationshipId")
        val relationshipId: String,
        @SerializedName("relationshipName")
        val relationshipName: String,
        @SerializedName("affidavitDoc")
        val affidavitDoc: AffidavitDoc,
        @SerializedName("nationalIdDoc")
        val nationalIdDoc: NationalIdDoc,
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String
    )
    data class AffidavitDoc(
        @SerializedName("code")
        val code: String, // GUARANTOR-AFFIDAVIT-DOC
        @SerializedName("documentId")
        val documentId: Int, // 181
        @SerializedName("url")
        val url: String  )
    data class NationalIdDoc(
        @SerializedName("code")
        val code: String, // GUARANTOR-AFFIDAVIT-DOC
        @SerializedName("documentId")
        val documentId: Int, // 181
        @SerializedName("url")
        val url: String   )
    data class CollateralDocument(
        @SerializedName("code")
        val code: String, // GUARANTOR-AFFIDAVIT-DOC
        @SerializedName("documentId")
        val documentId: Int, // 181
        @SerializedName("url")
        val url: String   )

    data class KinInfo(
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("identityNumber")
        val identityNumber: String,
        @SerializedName("identityType")
        val identityType: String,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("relationship")
        val relationship: String,
        @SerializedName("relationshipId")
        val relationshipId: String
    )

    data class OtherBorrowing(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("amountPaidToDate")
        val amountPaidToDate: String,
        @SerializedName("institutionName")
        val institutionName: String,
        @SerializedName("monthlyInstallmentPaid")
        val monthlyInstallmentPaid: String,
        @SerializedName("otherBorrowingId")
        val otherBorrowingId: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("statusId")
        val statusId: String
    )

    data class ResidenceInfo(
        @SerializedName("accommodationStatus")
        val accommodationStatus: String,
        @SerializedName("accommodationStatusId")
        val accommodationStatusId: String,
        @SerializedName("livingSince")
        val livingSince: String,
        @SerializedName("physicalAddress")
        val physicalAddress: String
    )

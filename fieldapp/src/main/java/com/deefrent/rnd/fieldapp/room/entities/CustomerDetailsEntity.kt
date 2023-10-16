package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity
class CustomerDetailsEntity {
    @PrimaryKey(autoGenerate = false)
    lateinit var nationalIdentity: String
    var lastStep: String = ""
    var isComplete = false
    var isProcessed = false
    var hasFinished = false
    var isButtonChecked = false
    var isBSButtonChecked = false
    var completion: String = ""
    var minimumCollateral: String = ""
    var maximumColateral: String = ""
    var minimumGuarantor: String = ""
    var maximumGuarantor: String = ""
    var bsDistrictId: String = ""
    var bsDistrict: String = ""
    var customerNumber: String = ""
    var bsEconomicFactorId: String = ""
    var bsEconomicFactor: String = ""
    var bsEstablishmentType: String = ""
    var bsEstablishmentTypeId: String = ""
    var bsNameOfIndustry: String = ""
    var bsNumberOfEmployees: String = ""
    var bsPhoneNumber: String = ""
    var bsPhysicalAddress: String = ""
    var bsTypeOfBusiness: String = ""
    var bsTypeOfBusinessId: String = ""
    var bsVillageId: String = ""
    var bsVillage = ""
    var alias = ""
    var bsYearsInBusiness: String = ""
    var dob: String = ""
    var educationLevelId: String = ""
    var educationLevel: String = ""
    var email: String = ""
    var employmentStatusId: String = ""
    var employmentStatus: String = ""
    var firstName: String = ""
    var genderId: String = ""
    var genderName: String = ""
    var howClientKnewMmfId: String = ""
    var howClientKnewMmf: String = ""
    var kinFirstName: String = ""
    var kinIdNumber: String = ""
    var kinIdentityTypeId: String = ""
    var kinIdentityType: String = ""
    var kinLastName: String = ""
    var kinPhoneNumber: String = ""
    var kinRelationshipId: String = ""
    var kinRelationship: String = ""
    var lastName: String = ""
    var numberOfChildren: String = ""
    var numberOfDependants: String = ""
    var phone: String = ""
    var resAccommodationStatusId: String = ""
    var resAccommodationStatus: String = ""
    var resLivingSince: String = ""
    var resPhysicalAddress: String = ""
    var spouseName: String = ""
    var spousePhone: String = ""
    var subBranchId: String = ""

    /**Expenses*/
    var rentalsExpenses: String = ""
    var food: String = ""
    var schoolFees: String = ""
    var transport: String = ""
    var medicalAidOrContributions: String = ""
    var otherExpenses: String = ""
    var totalExpenses: String = ""

    /**income*/
    var netSalary: String = ""
    var grossSalary: String = ""
    var totalSales: String = ""
    var profit: String = ""
    var rIncome: String = ""
    var donation: String = ""
    var otherIncome: String = ""
    var totalIncome: String = ""
    var assessmentRemarks: String = ""
    var customerId: String = ""
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerDetailsEntity::class,
        parentColumns = ["nationalIdentity"],
        childColumns = ["parentNationalIdentity"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class Collateral(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var parentNationalIdentity: String,
    var assetTypeId: Int,
    var assetType: String,
    var estimateValue: String,
    var model: String,
    var name: String,
    var serialNumber: String,
    var collateralGeneratedUID: String,
    var isSavedToRoom: Boolean
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerDetailsEntity::class,
        parentColumns = ["nationalIdentity"],
        childColumns = ["parentNationalIdentity"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class Guarantor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var idNumber: String,
    var parentNationalIdentity: String,
    var name: String,
    var phone: String,
    var relationshipId: Int,
    var relationship: String,
    var residenceAddress: String,
    var guarantorGeneratedUID: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerDetailsEntity::class,
        parentColumns = ["nationalIdentity"],
        childColumns = ["parentNationalIdentity"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class OtherBorrowing(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var institutionName: String,
    val parentNationalIdentity: String,
    var amount: String,
    var totalAmountPaidToDate: String,
    var status: Int,
    var monthlyInstallmentPaid: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerDetailsEntity::class,
        parentColumns = ["nationalIdentity"],
        childColumns = ["parentNationalIdentity"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class HouseholdMemberEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val parentNationalIdentity: String,
    var fullName: String,
    var incomeOrFeesPaid: String,
    val memberId: String,
    var natureOfActivity: String,
    var occupationId: String,
    var occupation: String,
    var relationshipId: String,
    var relationship: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerDetailsEntity::class,
        parentColumns = ["nationalIdentity"],
        childColumns = ["parentNationalIdentity"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class CustomerDocsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var parentNationalIdentity: String,
    var docCode: String,
    var docGeneratedUID: String,
    var docPath: String
)

data class CusomerDetailsEntityWithList(
    @Embedded val customerDetails: CustomerDetailsEntity,
    @Relation(
        parentColumn = "nationalIdentity",
        entityColumn = "parentNationalIdentity"
    ) val guarantors: List<Guarantor>,
    @Relation(
        parentColumn = "nationalIdentity",
        entityColumn = "parentNationalIdentity"
    ) val collateral: List<Collateral>,
    @Relation(
        parentColumn = "nationalIdentity",
        entityColumn = "parentNationalIdentity"
    ) val otherBorrowing: List<OtherBorrowing>,
    @Relation(
        parentColumn = "nationalIdentity",
        entityColumn = "parentNationalIdentity"
    )
    val householdMember: List<HouseholdMemberEntity>,
    @Relation(
        parentColumn = "nationalIdentity",
        entityColumn = "parentNationalIdentity"
    )
    val customerDocs: List<CustomerDocsEntity>
)



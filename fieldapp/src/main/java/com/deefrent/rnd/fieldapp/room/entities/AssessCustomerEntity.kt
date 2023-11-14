package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.*

@Entity
class AssessCustomerEntity {
    @PrimaryKey(autoGenerate = false)
    lateinit var idNumber: String
    var lastStep: String = ""
    var isComplete = false
    var isProcessed = false
    var minimumCollateral: String = ""
    var maximumColateral: String = "3"
    var minimumGuarantor: String = ""
    var maximumGuarantor: String = ""
    var hasFinished = false
    var phone: String = ""
    var assessmentRemarks: String = ""
    var firstName: String = "" // Mary
    var lastName: String = "" // Mary
    var gender: String = "" // Female
    var alsoKnownAs: String = ""
    var genderId: String = "" // 38
    var isMarried: Boolean = false
    var customerNumber: String = "" // 261
    var dob: String = ""
    var emailAddress: String = ""
    var spouseName: String = ""
    var spousePhone: String = ""
    var educationLevel: String = ""
    var educationLevelId: String = ""
    var identifier: String = "" // Recommendation
    var identifierId: String = "" // null
    var numberOfChildren: String = "" // 5
    var numberOfDependants: String = ""
    var empStatus: String = "" // Independent Professional
    var empStatusId: String = ""
    var businessTypeId: String = "" // null
    var businessTypeName: String = ""
    var economicFactorId: String = ""
    var economicFactorName: String = ""
    var nameOfIndustry: String = ""
    var establishmentTypeId: String = "" // null
    var establishmentTypeName: String = ""
    var yearsInBusiness: String = ""
    var businessPhysicalAddress: String = ""
    var businessDistrictId: String = "" // null
    var businessDistrictName: String = ""
    var businessVillageId: String = "" // null
    var businessVillageName: String = ""
    var businessPhone: String = ""
    var numberOfEmployees: String = ""
    var resPhysicalAddress: String = ""
    var resLivingSince: String = ""
    var resAccomodation: String = ""
    var resAccomadationStatus: String = ""
    var kinRelationshipId: String = ""
    var kinRelationship: String = ""
    var kinFirstName: String = ""
    var kinLastName: String = ""
    var kinPhoneNumber: String = ""
    var kinIdentityTypeId: String = ""
    var kinIdentityType: String = ""
    var kinIdNumber: String = ""
    var netSalary: String = ""
    var grossSalary: String = ""
    var totalSales: String = ""
    var profit: String = ""
    var rentalIncome: String = ""
    var donation: String = ""
    var otherIncome: String = ""
    var totalIncome: String = ""
    var expenseRentals: String = ""
    var expenseFood: String = ""
    var expenseSchoolFees: String = ""
    var expenseTransport: String = ""
    var expenseMedicalAidOrContributions: String = ""
    var otherExpenses: String = ""
    var assessmentPercentage: String = "" // 40.00
    var customerId: String = ""
    var subBranchId: String = ""
    var subBranch: String = ""
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = AssessCustomerEntity::class,
        parentColumns = ["idNumber"],
        childColumns = ["parentIdNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AssessCollateral(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var parentIdNumber: String,
    var assetTypeId: String,
    var assetType: String,
    var estimateValue: String,
    var model: String,
    var name: String,
    var serialNumber: String,
    var collateralGeneratedUID: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = AssessCustomerEntity::class,
        parentColumns = ["idNumber"],
        childColumns = ["parentIdNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AssessGuarantor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var idNumber: String,
    var parentIdNumber: String,
    var name: String,
    var phone: String,
    var relationshipId: Int,
    var relationship: String,
    var residenceAddress: String,
    var generatedUID: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = AssessCustomerEntity::class,
        parentColumns = ["idNumber"],
        childColumns = ["parentIdNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AssessBorrowing(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var institutionName: String,
    val parentIdNumber: String,
    var amount: String,
    var totalAmountPaidToDate: String,
    var statusId: Int,
    var status: String,
    var monthlyInstallmentPaid: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = AssessCustomerEntity::class,
        parentColumns = ["idNumber"],
        childColumns = ["parentIdNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AssessHouseholdMemberEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var parentIdNumber: String,
    var fullName: String,
    var incomeOrFeesPaid: String,
    var natureOfActivity: String,
    var occupation: String,
    var occupationId: String,
    var relationship: String,
    var relationshipId: String,

    )

@Entity(
    foreignKeys = [ForeignKey(
        entity = AssessCustomerEntity::class,
        parentColumns = ["idNumber"],
        childColumns = ["parentIdNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AssessCustomerDocsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val parentIdNumber: String,
    var docCode: String,
    var docGeneratedUID: String,
    var docPath: String
)


data class AssessCustomerEntityWithList(
    @Embedded val assessCustomerEntity: AssessCustomerEntity,
    @Relation(
        parentColumn = "idNumber",
        entityColumn = "parentIdNumber"
    )
    val assessCollateral: List<AssessCollateral>,
    @Relation(
        parentColumn = "idNumber",
        entityColumn = "parentIdNumber"
    )
    val assessGua: List<AssessGuarantor>,
    @Relation(
        parentColumn = "idNumber",
        entityColumn = "parentIdNumber"
    )
    val assessBorrow: List<AssessBorrowing>,
    @Relation(
        parentColumn = "idNumber",
        entityColumn = "parentIdNumber"
    )
    val householdMember: List<AssessHouseholdMemberEntity>,
    @Relation(
        parentColumn = "idNumber",
        entityColumn = "parentIdNumber"
    )
    val customerDocs: List<AssessCustomerDocsEntity>
)

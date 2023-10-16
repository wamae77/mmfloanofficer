package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CustomerIDLookupResponse(
    @SerializedName("data")
    val `data`: CustomerIDData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

data class CustomerIDData(
    val id:String,
    @SerializedName("customerNumber")
    val customerNumber: String,
    @SerializedName("assessmentPercentage")
    val assessmentPercentage: String,
    @SerializedName("assessmentRemarks")
    val assessmentRemarks: String,
    @SerializedName("expenses")
    val expenses: Expenses,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("householdMembers")
    val householdMembers: List<HouseholdMember>,
    @SerializedName("idNumber")
    val idNumber: String,
    @SerializedName("incomes")
    val incomes: Incomes,
    @SerializedName("isFullyRegistered")
    val isFullyRegistered: Boolean,
    @SerializedName("documentTypes")
    val documentTypes: List<DocumentType>,
    @SerializedName("lastName")
    val lastName: String
)

data class DocumentType(
    @SerializedName("id")
    val id: String, // 49
    @SerializedName("name")
    val name: String // Front National ID
)

data class Expenses(
    @SerializedName("domesticWorkersWages")
    val domesticWorkersWages: String,
    @SerializedName("food")
    val food: String,
    @SerializedName("funeralPolicy")
    val funeralPolicy: String,
    @SerializedName("medicalAidOrContributions")
    val medicalAidOrContributions: String,
    @SerializedName("other")
    val other: String,
    @SerializedName("rentals")
    val rentals: String,
    @SerializedName("schoolFees")
    val schoolFees: String,
    @SerializedName("tithe")
    val tithe: String,
    @SerializedName("transport")
    val transport: String
)

data class HouseholdMember(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("incomeOrFeesPaid")
    val incomeOrFeesPaid: String,
    @SerializedName("natureOfActivity")
    val natureOfActivity: String,
    @SerializedName("occupation")
    val occupation: String,
    @SerializedName("occupationId")
    val occupationId: String,
    @SerializedName("relationShip")
    val relationShip: String,
    @SerializedName("relationshipId")
    val relationshipId: String,
    @SerializedName("memberId")
    val memberId: Int,
)

data class Incomes(
    @SerializedName("incomeNetSalary")
    val incomeNetSalary: String, // 1000.0000
    @SerializedName("incomeProfit")
    val incomeProfit: String, // 1000.0000
    @SerializedName("incomeStatementDoc")
    val incomeStatementDoc: IncomeStatementDoc,
    @SerializedName("incomeTotalSales")
    val incomeTotalSales: String, // 1000.0000
    @SerializedName("other")
    val other: String, // 987.0000
    @SerializedName("otherBusinesses")
    val otherBusinesses: String, // 800.0000
    @SerializedName("ownSalary")
    val ownSalary: String, // 1000.0000
    @SerializedName("remittanceOrDonation")
    val remittanceOrDonation: String, // 900.0000
    @SerializedName("rental")
    val rental: String, // 100.0000
    @SerializedName("rentalDoc")
    val rentalDoc: RentalDoc,
    @SerializedName("roscals")
    val roscals: String, // 490.0000
    @SerializedName("salesReportDoc")
    val salesReportDoc: SalesReportDoc
)

data class IncomeStatementDoc(
    @SerializedName("code")
    val code: String, // INCOME-PAYSLIP-DOC
    @SerializedName("documentId")
    val documentId: Int, // 189
    @SerializedName("url")
    val url: String // null
)

data class RentalDoc(
    @SerializedName("code")
    val code: String, // INCOME-RENTAL-DOC
    @SerializedName("documentId")
    val documentId: Int, // 191
    @SerializedName("url")
    val url: String // https://test-api.ekenya.co.ke/moneymart-api/uploads/organizations/013/clients/373/documents/-INCOME-RENTAL-DOC.png
)

data class SalesReportDoc(
    @SerializedName("code")
    val code: String, // INCOME-SALES-REPORT-DOC
    @SerializedName("documentId")
    val documentId: Int, // 190
    @SerializedName("url")
    val url: String // https://test-api.ekenya.co.ke/moneymart-api/uploads/organizations/013/clients/373/documents/-INCOME-SALES-REPORT-DOC.png
)

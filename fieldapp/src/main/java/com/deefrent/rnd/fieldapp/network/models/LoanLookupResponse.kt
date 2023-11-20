package com.deefrent.rnd.fieldapp.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class LoanLookupResponse(
    @SerializedName("data")
    val `data`: LoanLookupData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class LoanLookupByNameResponse(
    @SerializedName("data")
    val `data`: List<LoanLookupData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class LoanLookupData(
    @SerializedName("canApplyLoan")
    val canApplyLoan: Boolean,
    @SerializedName("id")
    val clientId: Int,
    val creditRating: String,
    val isAssessed: Boolean,
    @SerializedName("canDisburse")
    val canDisburse: Boolean,
    @SerializedName("canRepay")
    val canRepay: Boolean,
    @SerializedName("canCashOut")
    val canCashOut: Boolean,
    @SerializedName("customerNumber")
    val customerNumber: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("idNumber")
    val idNumber: String,
    @SerializedName("fingerPrintRegId")
    val fingerPrintRegId: String,
    @SerializedName("isFullyRegistered")
    val isFullyRegistered: Boolean,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone:String,
    @SerializedName("periodMeasures")
    val periodMeasures: List<PeriodMeasure>,
    @SerializedName("loanPurposes")
    val loanPurposes: List<LoanPurposes>,
    @SerializedName("disbursableLoans")
    val disbursableLoans: List<DisbursableLoan>,
    @SerializedName("repayableLoans")
    val repayableLoans: List<RepayableLoan>,
    @SerializedName("loansPendingApproval")
    val loansPendingApproval: List<LoansPendingApproval>,
    @SerializedName("loanHistory")
    val loanHistory: List<LoanHistory>,
    @SerializedName("products")
    val products: List<LoanProduct>,
    @SerializedName("walletAccounts")
    val walletAccounts: List<WalletAccount>
)

@Parcelize
data class RepayableLoan(
    @SerializedName("amountApplied")
    val amountApplied: String, // 200
    @SerializedName("amountApproved")
    val amountApproved: String, // 20000.0000
    @SerializedName("applicationDate")
    val applicationDate: String, // 2022-03-04
    @SerializedName("balance")
    val balance: String, // 180.0000
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("loanAccountNo")
    val loanAccountNo: String, // AG18LN
    @SerializedName("loanId")
    val loanId: Int, // 244
    @SerializedName("name")
    val name: String, // BUSINESS LOAN
    @SerializedName("productId")
    val productId: Int, // 43
    @SerializedName("remainingAmount")
    val remainingAmount: String, // 0.0000,
    @SerializedName("loanTenure")
    val loanTenure: String,
    @SerializedName("interestRate")
    val interestRate: String,

    var customerNumber: String?
) : Parcelable

data class LoanPurposes(
    @SerializedName("id")
    val id: Int, // 1
    @SerializedName("name")
    val name: String // Emergency
) {
    override fun toString(): String {
        return name
    }
}

@Parcelize
data class DisbursableLoan(
    @SerializedName("amountApplied")
    val amountApplied: String, // 3
    @SerializedName("amountApproved")
    val amountApproved: String, // 20000.0000
    @SerializedName("remainingAmount")
    val remainingAmount: String,
    @SerializedName("applicationDate")
    val applicationDate: String, // 2022-03-20
    @SerializedName("balance")
    val balance: String, // 0.0000
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("loanAccountNo")
    val loanAccountNo: String, // SL18LN
    @SerializedName("loanId")
    val loanId: Int, // 261
    @SerializedName("name")
    val name: String,
    @SerializedName("loanTenure")
    val loanTenure: String,
    @SerializedName("interestRate")
    val interestRate: String,
    @SerializedName("productId")
    val productId: Int // 44
) : Parcelable

@Parcelize
data class LoanProduct(
    @SerializedName("interestRate")
    val interestRate: String,
    @SerializedName("limit")
    val limit: String,
    @SerializedName("maxRepaymentPeriod")
    val maxRepaymentPeriod: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("productId")
    val productId: Int
) : Parcelable

data class LoanHistory(
    @SerializedName("amountApplied")
    val amountApplied: String,
    @SerializedName("amountApproved")
    val amountApproved: String,
    @SerializedName("amountDisbursed")
    val amountDisbursed: String,
    @SerializedName("amountRepaid")
    val amountRepaid: String,
    @SerializedName("applicationDate")
    val applicationDate: String,
    @SerializedName("disbursementDate")
    val disbursementDate: String,
    @SerializedName("lastAmountRepaid")
    val lastAmountRepaid: String,
    @SerializedName("loanBalance")
    val loanBalance: String,
    @SerializedName("loanId")
    val loanId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("productCode")
    val productCode: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("loanNumber")
    val loanNumber: String,
    @SerializedName("currency")
    val currency: String
)

@Parcelize
data class LoansPendingApproval(
    @SerializedName("amountApplied")
    val amountApplied: String, // 500
    @SerializedName("amountApproved")
    val amountApproved: String, // 0
    @SerializedName("applicationDate")
    val applicationDate: String, // 2022-04-13
    @SerializedName("balance")
    val balance: String, // 0.0000
    @SerializedName("currency")
    val currency: String, // USD
    @SerializedName("decodedPaymentCycleMeasure")
    val decodedPaymentCycleMeasure: String, // Month(s)
    @SerializedName("decodedPaymentPeriodMeasure")
    val decodedPaymentPeriodMeasure: String, // Month(s)
    @SerializedName("loanAccountNo")
    val loanAccountNo: String, // SL24LN
    @SerializedName("loanId")
    val loanId: Int, // 290
    @SerializedName("loanOfficerAmount")
    val loanOfficerAmount: String, // 0
    @SerializedName("loanOfficerRemarks")
    val loanOfficerRemarks: String,
    @SerializedName("name")
    val name: String, // SOLAR SYSTEM LOAN
    @SerializedName("paymentCycle")
    val paymentCycle: String, // 1
    @SerializedName("paymentCycleMeasure")
    val paymentCycleMeasure: String, // 4
    @SerializedName("paymentPeriod")
    val paymentPeriod: String, // 6
    @SerializedName("paymentPeriodMeasure")
    val paymentPeriodMeasure: String, // 4
    @SerializedName("productId")
    val productId: Int, // 44
    @SerializedName("remainingAmount")
    val remainingAmount: String // 0
) : Parcelable

data class PeriodMeasure(
    @SerializedName("id")
    val id: Int, // 2
    @SerializedName("label")
    val label: String // Day(s)
) {
    override fun toString(): String {
        return label
    }
}

data class WalletAccount(
    @SerializedName("accountName")
    val accountName: String,
    @SerializedName("accountNumber")
    val accountNumber: String,
    @SerializedName("availableBalance")
    val availableBalance: String,
    @SerializedName("currentBalance")
    val currentBalance: String,
    @SerializedName("defaultCurrency")
    val defaultCurrency: String
) {
    override fun toString(): String {
        val accNo = accountNumber.replace("(?<=.{2}).(?=.{2})".toRegex(), "*")
        return "$accountName - $accNo"
    }
}



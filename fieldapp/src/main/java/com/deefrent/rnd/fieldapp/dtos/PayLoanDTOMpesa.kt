package com.deefrent.rnd.fieldapp.dtos

data class PayLoanDTOMpesa(
    val loanId: String,
    val amount: String,
    val providerId: Int = 3,
    val providerPhone: String,
    val payAll: Int,
    val idNumber: String,
    val accountId: String="",
    val channel: String="",
    val loanAccountNo: String,
    val description: String = "",
    val repaymentDate: String = ""
)

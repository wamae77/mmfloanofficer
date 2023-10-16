package com.deefrent.rnd.fieldapp.dtos.billers

data class BillPaymentDTO(
    var customerIdNumber: String,
    var amount: String,
    var transactionType: String,
    var walletAccountNumber: String,
    var phoneNumber: String,
    var accountNumber: String,
    var billerCode: String,
    var academicSemester: String,
    var recipientFirstName: String,
    var recipientLastName: String,
    var recipientIdNumber: String,
)

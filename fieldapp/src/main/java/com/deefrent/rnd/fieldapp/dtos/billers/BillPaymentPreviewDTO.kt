package com.deefrent.rnd.fieldapp.dtos.billers

data class BillPaymentPreviewDTO(
    var amount: String,
    var walletAccountNumber: String,
    var billerAccountNumber: String,
    var billerCode: String
)

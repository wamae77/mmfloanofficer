package com.deefrent.rnd.fieldapp.dtos.customer

data class GetWalletAccountsDTO(
    val customerIdNumber: String,
    val isTransactional: Int
)
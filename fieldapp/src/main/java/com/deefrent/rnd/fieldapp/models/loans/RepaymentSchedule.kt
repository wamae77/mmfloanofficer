package com.deefrent.rnd.fieldapp.models.loans

import androidx.annotation.Keep

@Keep
data class RepaymentSchedule(
    val dateDue: String,
    val scheduleNo: String,
    val totalBalance: String,
    val totalInstallment: String,
    val totalRepaid: String
)
package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.loans.MemberInformation
import com.deefrent.rnd.fieldapp.models.loans.Transaction

@Keep
data class LoanStatementData(
    val memberInformation: MemberInformation,
    val transactions: List<Transaction>
)
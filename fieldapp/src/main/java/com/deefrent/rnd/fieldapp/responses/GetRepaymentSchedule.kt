package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.loans.RepaymentSchedule

@Keep
data class GetRepaymentSchedule(
    val `data`: List<RepaymentSchedule>,
    val message: String,
    val status: Int
)
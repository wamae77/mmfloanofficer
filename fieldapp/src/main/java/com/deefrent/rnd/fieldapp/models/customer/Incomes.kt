package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.network.models.IncomeStatementDoc
import com.deefrent.rnd.fieldapp.network.models.RentalDoc
import com.deefrent.rnd.fieldapp.network.models.SalesReportDoc

@Keep
data class Incomes(
    val incomeNetSalary: String,
    val incomeProfit: String,
    val incomeStatementDoc: IncomeStatementDoc,
    val incomeTotalSales: String,
    val other: String,
    val otherBusinesses: String,
    val ownSalary: String,
    val remittanceOrDonation: String,
    val rental: String,
    val rentalDoc: RentalDoc,
    val roscals: String,
    val salesReportDoc: SalesReportDoc
)
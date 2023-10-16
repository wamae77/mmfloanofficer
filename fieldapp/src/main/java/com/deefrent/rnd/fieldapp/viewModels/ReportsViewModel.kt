package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.models.merchantAgentReport.GetTransactionsReportResponse
import com.deefrent.rnd.fieldapp.repositories.ReportsRepo

class ReportsViewModel:ViewModel() {
    private var reportsRepo:ReportsRepo = ReportsRepo()
    fun getTransactionsReport(date:String): LiveData<GetTransactionsReportResponse?> {
        return reportsRepo.getTransactionsReport(date)
    }
}
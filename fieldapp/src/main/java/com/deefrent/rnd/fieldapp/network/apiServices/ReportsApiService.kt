package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.models.merchantAgentReport.GetTransactionsReportResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportsApiService {
    @GET("portal/sales/get-transactions")
    fun getTransactionsReport(@Query("date") date: String): Call<GetTransactionsReportResponse>
}
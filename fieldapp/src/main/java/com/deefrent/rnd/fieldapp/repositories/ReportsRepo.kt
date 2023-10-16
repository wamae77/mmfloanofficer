package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.models.merchantAgentReport.GetTransactionsReportResponse
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.ReportsApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportsRepo {
    private var reportsApiService: ReportsApiService =
        ApiClient.getRetrofit()?.create(ReportsApiService::class.java)!!

    fun getTransactionsReport(date:String): LiveData<GetTransactionsReportResponse?> {
        val data = MutableLiveData<GetTransactionsReportResponse?>()
        reportsApiService.getTransactionsReport(date)
            .enqueue(object : Callback<GetTransactionsReportResponse> {
                override fun onResponse(
                    call: Call<GetTransactionsReportResponse>,
                    response: Response<GetTransactionsReportResponse>
                ) {
                    data.value = response.body()
                    /*Log.d("response", "response body: ${response.body().toString()}")
                    try {
                            Log.d(
                                "response",
                                "response error: ${Gson().toJson(response.errorBody())}"
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.d("Auth Repo", "response exception:" + e.localizedMessage)
                        }*/
                }

                override fun onFailure(call: Call<GetTransactionsReportResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Reports Repo", t.localizedMessage)
                }
            })
        return data
    }
}
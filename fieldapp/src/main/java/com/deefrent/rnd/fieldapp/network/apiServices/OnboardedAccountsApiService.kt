package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.models.onboardedAccounts.GetOnboardedAccountsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OnboardedAccountsApiService {
    @GET("dsr/registered")
    fun getOnboardedAccounts(
        @Query("startdate") startdate: String,
        @Query("enddate") enddate: String,
        @Query("size") size: String,
        @Query("page") page: String
    ): Call<GetOnboardedAccountsResponse>
}
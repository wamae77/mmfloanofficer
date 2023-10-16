package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.models.dsrProfile.GetDsrProfileResponse
import retrofit2.Call
import retrofit2.http.GET

interface DsrProfileApiService {
    @GET("customer/get-user-profile-details")
    fun getDsrProfile(): Call<GetDsrProfileResponse>
}
package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.bodies.auth.GetUserDetailsBody
import com.deefrent.rnd.fieldapp.responses.GetUserDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/auth/get-userdetails")
    fun getUserDetails(@Body getUserDetailsBody: GetUserDetailsBody): Call<GetUserDetailsResponse>
}
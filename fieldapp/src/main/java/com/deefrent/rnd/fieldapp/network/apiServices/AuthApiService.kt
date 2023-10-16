package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("identity-server/authorization")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String
    ): Call<LoginResponse>

}
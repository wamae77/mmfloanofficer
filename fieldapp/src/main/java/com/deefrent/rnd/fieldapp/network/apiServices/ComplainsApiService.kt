package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.responses.CreateComplainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ComplainsApiService {
    @Multipart
    @POST("customer/create-complain")
    fun createComplain(
        @Part("complainDetails") merchDetails: RequestBody,
        @Part complainFiles: MultipartBody.Part
    ): Call<CreateComplainResponse>
}
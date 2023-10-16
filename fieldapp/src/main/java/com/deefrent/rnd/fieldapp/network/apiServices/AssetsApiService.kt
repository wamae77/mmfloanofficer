package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.responses.SubmitAssetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AssetsApiService {
    @Multipart
    @POST("customer/create-asset-management")
    fun submitAsset(
        @Part("assetDetails") merchDetails: RequestBody,
        @Part assetFiles: MultipartBody.Part
    ): Call<SubmitAssetResponse>
}
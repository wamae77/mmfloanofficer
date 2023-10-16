package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.AssetsApiService
import com.deefrent.rnd.fieldapp.responses.SubmitAssetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssetsRepo {
    private var assetsApiService: AssetsApiService =
        ApiClient.getRetrofit()?.create(AssetsApiService::class.java)!!

    fun submitAsset(assetDetails: RequestBody,
                    assetFiles: MultipartBody.Part): MutableLiveData<SubmitAssetResponse?> {
        val data = MutableLiveData<SubmitAssetResponse?>()
        assetsApiService.submitAsset(assetDetails,assetFiles)
            .enqueue(object : Callback<SubmitAssetResponse> {
                override fun onResponse(
                    call: Call<SubmitAssetResponse>,
                    response: Response<SubmitAssetResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<SubmitAssetResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Assets Repo", t.localizedMessage)
                }
            })
        return data
    }
}
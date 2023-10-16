package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.ComplainsApiService
import com.deefrent.rnd.fieldapp.responses.CreateComplainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplainsRepo {
    private var complainsApiService: ComplainsApiService =
        ApiClient.getRetrofit()?.create(ComplainsApiService::class.java)!!

    fun createComplain(complainDetails: RequestBody, complainFiles: MultipartBody.Part):
            MutableLiveData<CreateComplainResponse?> {
        val data = MutableLiveData<CreateComplainResponse?>()
        complainsApiService.createComplain(complainDetails, complainFiles)
            .enqueue(object : Callback<CreateComplainResponse> {
                override fun onResponse(
                    call: Call<CreateComplainResponse>,
                    response: Response<CreateComplainResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<CreateComplainResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Complains Repo", t.localizedMessage)
                }
            })
        return data
    }
}
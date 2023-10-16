package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.models.dsrProfile.GetDsrProfileResponse
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.DsrProfileApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DsrProfileRepo {
    private var dsrProfileApiService: DsrProfileApiService =
        ApiClient.getRetrofit()?.create(DsrProfileApiService::class.java)!!

    fun getDsrProfile(): LiveData<GetDsrProfileResponse> {
        val data = MutableLiveData<GetDsrProfileResponse>()
        dsrProfileApiService.getDsrProfile()
            .enqueue(object : Callback<GetDsrProfileResponse> {
                override fun onResponse(
                    call: Call<GetDsrProfileResponse>,
                    response: Response<GetDsrProfileResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<GetDsrProfileResponse>, t: Throwable) {
                    data.value = null
                    Log.d("DSR profile Repo", t.localizedMessage)
                }
            })
        return data
    }
}
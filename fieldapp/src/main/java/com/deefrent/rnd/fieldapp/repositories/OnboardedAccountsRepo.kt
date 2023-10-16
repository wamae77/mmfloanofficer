package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.GetOnboardedAccountsResponse
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.OnboardedAccountsApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnboardedAccountsRepo {
    private var onboardedAccountsApiService: OnboardedAccountsApiService =
        ApiClient.getRetrofit()?.create(OnboardedAccountsApiService::class.java)!!

    fun getOnboardedAccounts(
        startdate: String,
        enddate: String,
        size: String,
        page: String
    ): LiveData<GetOnboardedAccountsResponse?> {
        val data = MutableLiveData<GetOnboardedAccountsResponse?>()
        onboardedAccountsApiService.getOnboardedAccounts(startdate, enddate, size, page)
            .enqueue(object : Callback<GetOnboardedAccountsResponse> {
                override fun onResponse(
                    call: Call<GetOnboardedAccountsResponse>,
                    response: Response<GetOnboardedAccountsResponse>
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

                override fun onFailure(call: Call<GetOnboardedAccountsResponse>, t: Throwable) {
                    data.value = null
                        Log.d("Onboarded Accounts Repo", t.localizedMessage)
                }
            })
        return data
    }
}
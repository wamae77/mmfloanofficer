package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.bodies.existingAccounts.SearchExistingAccountBody
import com.deefrent.rnd.fieldapp.models.customerDetails.CustomerDetailsResponse
import com.deefrent.rnd.fieldapp.models.customerGeomap.CustomerGeomapResponse
import com.deefrent.rnd.fieldapp.models.merchantAgentDetails.MerchantAgentDetailsResponse
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.ExistingAccountApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExistingAccountRepo {
    private var existingAccountApiService: ExistingAccountApiService =
        ApiClient.getRetrofit()?.create(ExistingAccountApiService::class.java)!!

    fun getCustomerDetails(searchExistingAccountBody: SearchExistingAccountBody): LiveData<CustomerDetailsResponse> {
        val data = MutableLiveData<CustomerDetailsResponse>()
        existingAccountApiService.getCustomerDetails(searchExistingAccountBody)
            .enqueue(object : Callback<CustomerDetailsResponse> {
                override fun onResponse(
                    call: Call<CustomerDetailsResponse>,
                    response: Response<CustomerDetailsResponse>
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

                override fun onFailure(call: Call<CustomerDetailsResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Existing Account Repo", t.localizedMessage)
                }
            })
        return data
    }

    fun getMerchantAgentDetails(accountNo: String): LiveData<MerchantAgentDetailsResponse> {
        val data = MutableLiveData<MerchantAgentDetailsResponse>()
        existingAccountApiService.getMerchantAgentDetails(accountNo)
            .enqueue(object : Callback<MerchantAgentDetailsResponse> {
                override fun onResponse(
                    call: Call<MerchantAgentDetailsResponse>,
                    response: Response<MerchantAgentDetailsResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<MerchantAgentDetailsResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Existing Account Repo", t.localizedMessage)
                }
            })
        return data
    }

    fun getCustomerGeomapDetails(accountNo: String): LiveData<CustomerGeomapResponse> {
        val data = MutableLiveData<CustomerGeomapResponse>()
        existingAccountApiService.getCustomerGeomapDetails(accountNo)
            .enqueue(object : Callback<CustomerGeomapResponse> {
                override fun onResponse(
                    call: Call<CustomerGeomapResponse>,
                    response: Response<CustomerGeomapResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<CustomerGeomapResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Existing Account Repo", t.localizedMessage)
                }
            })
        return data
    }
}
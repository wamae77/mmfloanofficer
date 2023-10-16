package com.deefrent.rnd.fieldapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.fieldapp.network.apiClients.AuthApiClient
import com.deefrent.rnd.fieldapp.network.apiServices.AuthApiService
import com.deefrent.rnd.fieldapp.responses.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepo {
    private var authApiService: AuthApiService =
        AuthApiClient.getRetrofitLogin()?.create(AuthApiService::class.java)!!

    fun loginUser(username: String, password: String, grant_type: String): LiveData<LoginResponse> {
        val data = MutableLiveData<LoginResponse>()
        authApiService.loginUser(username, password, grant_type)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    /*try {
                        Log.d(
                            "Auth Repo",
                            "onResponse: ${Gson().toJson(response.errorBody().toString())}"
                        );
                    } catch (e: IOException) {
                        e.printStackTrace();
                        Log.d("Auth Repo", "onResponse:" + e.localizedMessage);
                    }*/
                    response.toString()
                    data.value = response.body()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Data Repo", t.localizedMessage)
                }
            })
        return data
    }

}
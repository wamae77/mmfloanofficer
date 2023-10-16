package com.deefrent.rnd.fieldapp.network.apiClients

import com.deefrent.rnd.fieldapp.BuildConfig
import com.deefrent.rnd.fieldapp.network.interceptors.BasicAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthApiClient {
    companion object{

        fun getRetrofitLogin(): Retrofit? {
            var retrofit: Retrofit? = null
            if (retrofit == null) {
                val interceptor= BasicAuthInterceptor("BuildConfig.OAuth2Username","BuildConfig.OAuth2Password")
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.AUTH_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}
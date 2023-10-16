package com.deefrent.rnd.fieldapp.network.apiClients

import com.deefrent.rnd.fieldapp.BuildConfig
import com.deefrent.rnd.fieldapp.network.interceptors.TokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*private val logger = run {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.apply {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }
}
private val okHttpClient = if (BuildConfig.DEBUG) {
    OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(2 * 60, TimeUnit.SECONDS)
        .callTimeout(2 * 60, TimeUnit.SECONDS)
        .addInterceptor(logger)
        .addInterceptor(TokenInterceptor())
        .build()
} else {
    OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(2 * 60, TimeUnit.SECONDS)
        .callTimeout(2 * 60, TimeUnit.SECONDS)
        .addInterceptor(TokenInterceptor())
        .build()
}*/

class ApiClient {
    companion object {

        fun getRetrofit(): Retrofit? {
            var retrofit: Retrofit? = null
            if (retrofit == null) {
                val interceptor = TokenInterceptor()

                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
                //implement certificate pinner(sha to be provided by backend person)

                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }


}
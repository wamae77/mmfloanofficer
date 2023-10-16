package com.deefrent.rnd.common.network

import android.util.Log
import com.deefrent.rnd.common.BuildConfig
import com.deefrent.rnd.common.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object FieldInstance {
    /**define our own request timeout so that retrofit doesnt use its default one for 10 sec*/

    private const val REQUEST_TIMEOUT = 60

    /**
     * Sets the certificate pinner that constrains which certificates are trusted.
     * Pinning certificates avoids the need to trust certificate authorities.
     * Also ensure app are free from attackers thru fake certs.
     */
    var certificatePinner = CertificatePinner.Builder()
        .add(Constants.PINNER_URL, Constants.PINNER_CERT)
        .build()

    var spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .build()

    /**Interceptor helps logs outing request and the incoming response */
    var logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    /**configure timeouts settings via OkHTTP*/
    val httpClient = if (BuildConfig.DEBUG) {
        OkHttpClient().newBuilder()
            .addInterceptor(logging)
            .connectTimeout(30.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain: Interceptor.Chain ->
                /** interceptors uses chain.request() to acquire the original request from the app and set it to originalRequest,
                Then, we build the request again by adding the Header with the key and value which is required to make the network call.
                Then, we will build the request again and pass the new request which is having the Authorization header using chain.proceed(request)
                 */
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("version-name", "1.0.0")
                    .addHeader("Authorization", " Bearer ${Constants.token}")
                    .addHeader("Device-ID", Constants.DEVICE_ID)
                    //.addHeader("Authorization", Constants.token)
                    .addHeader("app-id", "1")
                val request = requestBuilder.build()
                Log.d("INstance", "REQUEST HEADERS $request")
                chain.proceed(request)
            }
            .certificatePinner(certificatePinner)
            .connectionSpecs(listOf(spec))
            .build()
    } else {
        OkHttpClient().newBuilder()
            //.addInterceptor(logging)
            .connectTimeout(30.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain: Interceptor.Chain ->
                /** interceptors uses chain.request() to acquire the original request from the app and set it to originalRequest,
                Then, we build the request again by adding the Header with the key and value which is required to make the network call.
                Then, we will build the request again and pass the new request which is having the Authorization header using chain.proceed(request)
                 */
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("version-name", "1.0.0")
                    .addHeader("Authorization", " Bearer ${Constants.token}")
                    .addHeader("Device-ID", Constants.DEVICE_ID)
                    //.addHeader("Authorization", Constants.token)
                    .addHeader("app-id", "1")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .certificatePinner(certificatePinner)
            .connectionSpecs(listOf(spec))
            .build()
    }
    /**retrofit instance*/
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(httpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}
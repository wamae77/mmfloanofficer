package com.deefrent.rnd.common.network


import com.deefrent.rnd.common.BuildConfig
import com.deefrent.rnd.common.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkInterceptor: NetworkInterceptor
) {
    /**define our own request timeout so that retrofit doesn't use its default one for 10 sec*/
    private val REQUEST_TIMEOUT = 30L

    /**
     * This is a reusable function to perform API instances
     */
    fun <API> buildApi(api: Class<API>, baseUrl: String): API {
        return Retrofit.Builder()
            .baseUrl(baseUrl.toString().trim())
            .client(getOkHttpClient(networkInterceptor))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .build()
            .create(api)
    }

    fun <API> buildApiForFingerPrint(api: Class<API>, baseUrl: String): API {
        return Retrofit.Builder()
            .baseUrl(baseUrl.toString().trim())
            .client(getOkHttpClientForFingerPrint())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .build()
            .create(api)
    }

    /**
     * Interceptor helps logs outing request and the incoming response
     * */
    private fun getOkHttpClient(interceptor: Interceptor): OkHttpClient {
        /**
         * Pinning certificates defends against attacks on certificate authorities.
         * It also prevents connections through man-in-the-middle certificate
         * authorities either known or unknown to the application's user.
         */
        /**
         * Sets the certificate pinner that constrains which certificates are trusted.
         * Pinning certificates avoids the need to trust certificate authorities.
         * Also ensure app are free from attackers thru fake certs.
         */
        val certificatePinner = CertificatePinner.Builder()
            .add(Constants.PINNER_URL.toString(), Constants.PINNER_CERT.toString())
            .build()

        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()
        val specs = listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)

        return OkHttpClient().newBuilder().apply {
            /**configure timeouts settings via OkHTTP*/
            connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor =
                    HttpLoggingInterceptor().setLevel(level = HttpLoggingInterceptor.Level.BODY)
                addInterceptor(loggingInterceptor)
            }
            addInterceptor(interceptor)
            addInterceptor { chain ->
                chain.proceed(chain.request()).newBuilder().also {
                    it.addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("version-name", "1.0.0")
                        .addHeader("Authorization", " Bearer ${Constants.token}")
                        .addHeader("Device-ID", Constants.DEVICE_ID)
                        .addHeader("app-id", "1")
                }.build()
            }
            certificatePinner(certificatePinner)
            connectionSpecs(Collections.singletonList(spec))
            //connectionSpecs(specs)

        }.build()
    }

    private fun getOkHttpClientForFingerPrint(): OkHttpClient {
        /**
         * Pinning certificates defends against attacks on certificate authorities.
         * It also prevents connections through man-in-the-middle certificate
         * authorities either known or unknown to the application's user.
         */
        /**
         * Sets the certificate pinner that constrains which certificates are trusted.
         * Pinning certificates avoids the need to trust certificate authorities.
         * Also ensure app are free from attackers thru fake certs.
         */
        var certificatePinner = CertificatePinner.Builder()
            .add(Constants.PINNER_URL.toString(), Constants.PINNER_CERT.toString())
            .build()

        //val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS,)
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()
        val specs = listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)

        return OkHttpClient().newBuilder().apply {
            /**configure timeouts settings via OkHTTP*/
            connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor =
                    HttpLoggingInterceptor().setLevel(level = HttpLoggingInterceptor.Level.BODY)
                addInterceptor(loggingInterceptor)
            }
            // addInterceptor(interceptor)
            addInterceptor { chain ->
                chain.proceed(chain.request()).newBuilder().also {
                    it.addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("version-name", "1.0.0")
                        .addHeader("app-id", "1")
                }.build()
            }
            //certificatePinner(certificatePinner)
            //connectionSpecs(Collections.singletonList(spec))
            // connectionSpecs(specs)

        }.build()
    }
}

package com.deefrent.rnd.fieldapp.network.repos

import android.util.Base64
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.common.data.fingerprint.FingerPrintEnrollmentResponse
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.*
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.*
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**retrofit interface, where to define all our api services*/
interface FingerPrintApiService {

    @Multipart
    @POST("Users/create")
    suspend fun enrollUser(
        @Header("Authorization") authorization: String = BASIC,
        @Header("service_name") service_name: String = Constants.FINGERPRINT_SERVICENAME,
        @Part("phone") phone: RequestBody,
        @Part("finger_index") finger_index: RequestBody,
        @Part("hand_type") hand_type: RequestBody,
        @Part image: MultipartBody.Part? = null,
    ): CommonResponse

    @Multipart
    @POST("Users/login_users")
    suspend fun loginUsersWithFingerPrint(
        @Header("Authorization") authorization: String = BASIC,
        @Header("service_name") service_name: String = Constants.FINGERPRINT_SERVICENAME,
        @Part("user_uid") user_uid: RequestBody,
        @Part candidate_print: MultipartBody.Part? = null,
    ): CommonResponse


    @POST("Users/enroll")
    @Multipart
    suspend fun enrollWithMultipleImages(
        @Header("Authorization") authorization: String = BASIC,
        @Header("service_name") service_name: String = Constants.FINGERPRINT_SERVICENAME,
        @Part("id_number") id_number: RequestBody,
        @Part("finger_index") finger_index: RequestBody,
        @Part("hand_type") hand_type: RequestBody,
        @Part images: List<MultipartBody.Part?>?
    ): FingerPrintEnrollmentResponse

    @Multipart
    @POST("Users/verify/many/")
    suspend fun loginUsersWithMultipleImages(
        @Header("Authorization") authorization: String = BASIC,
        @Header("service_name") service_name: String = Constants.FINGERPRINT_SERVICENAME,
        @Part("user_uid") user_uid: RequestBody,
        @Part candidate_print: MultipartBody.Part? = null,
    ): CommonResponse
}

//consumer_key + consumer_secret
/*
    {
        "consumer key": "04b155b3fbb2ecf020230629-174319992c",
        "consumer secret": "bff662abffd1969004b155b3fbb2ecf020230629-174319992c67dd20230629-174319"
    }
*/
private val CREDENTIALS: String =
    Constants.FINGERPRINT_KEY + ":" + Constants.FINGERPRINT_SECRET

//private val CREDENTIALS: String = "2ea2abffd3a816e720230816-10444151a0" + ":" + "b3a8c663bf6d0dd22ea2abffd3a816e720230816-10444151a08c5e20230816-104441"

// create Base64 encodet string
val BASIC = "Basic " + Base64.encodeToString(CREDENTIALS.toByteArray(), Base64.NO_WRAP)


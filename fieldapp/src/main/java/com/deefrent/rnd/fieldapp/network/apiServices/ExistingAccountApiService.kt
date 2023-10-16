package com.deefrent.rnd.fieldapp.network.apiServices

import com.deefrent.rnd.fieldapp.bodies.existingAccounts.SearchExistingAccountBody
import com.deefrent.rnd.fieldapp.models.customerDetails.CustomerDetailsResponse
import com.deefrent.rnd.fieldapp.models.customerGeomap.CustomerGeomapResponse
import com.deefrent.rnd.fieldapp.models.merchantAgentDetails.MerchantAgentDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ExistingAccountApiService {
    @POST("customer/v2/get-customer-details")
    fun getCustomerDetails(@Body searchExistingAccountBody: SearchExistingAccountBody): Call<CustomerDetailsResponse>

    @POST("merchant/get-merchant-details/{accountNo}")
    fun getMerchantAgentDetails(@Path("accountNo") accountNo: String): Call<MerchantAgentDetailsResponse>

    @POST("merchant/get-merchant-detail-geo-map-details/{accountNo}")
    fun getCustomerGeomapDetails(@Path("accountNo") accountNo: String): Call<CustomerGeomapResponse>
}
package com.deefrent.rnd.fieldapp.network.apiServices;

import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateCustomerBody;
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateMerchantBody;
import com.deefrent.rnd.fieldapp.responses.OnboardCustomerResponse;
import com.deefrent.rnd.fieldapp.responses.OnboardMerchantAgentResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OnboardAccountApiService {
    @POST("customer/create-customer-account")
    Call<OnboardCustomerResponse> onboardCustomer(@Body CreateCustomerBody createCustomerBody);

    @POST("merchant/create-merchant-account")
    Call<OnboardMerchantAgentResponse> onboardMerchantAgent(@Body CreateMerchantBody createMerchantBody);

    @Multipart
    @POST("customer/create-customer-account")
    Call<OnboardCustomerResponse> onboardIndividualAccount(@Part("customerdata") RequestBody customerdata,
                                                           @Part MultipartBody.Part frontIdCapture,
                                                           @Part MultipartBody.Part backIdCapture,
                                                           @Part MultipartBody.Part passportPhotoCapture);

    @Multipart
    @POST("merchant/create-merchant-account")
    Call<OnboardMerchantAgentResponse> onboardMerchantAgentAccount(@Part("merchDetails") RequestBody merchDetails,
                                                                   @Part MultipartBody.Part termsAndConditionDoc,
                                                                   @Part MultipartBody.Part customerPhoto,
                                                                   @Part MultipartBody.Part signatureDoc,
                                                                   @Part MultipartBody.Part businessPermitDoc,
                                                                   @Part MultipartBody.Part companyRegistrationDoc,
                                                                   @Part MultipartBody.Part frontIDFile,
                                                                   @Part MultipartBody.Part backIDFile,
                                                                   @Part MultipartBody.Part goodConductFile,
                                                                   @Part MultipartBody.Part fieldApplicationFormFile,
                                                                   @Part MultipartBody.Part kraPinFile,
                                                                   @Part MultipartBody.Part businessLicenseFile,
                                                                   @Part MultipartBody.Part shopPhotoFile);
}

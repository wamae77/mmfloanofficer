package com.deefrent.rnd.fieldapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateCustomerBody;
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateMerchantBody;
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient;
import com.deefrent.rnd.fieldapp.network.apiServices.OnboardAccountApiService;
import com.deefrent.rnd.fieldapp.utils.IExecute;
import com.deefrent.rnd.fieldapp.responses.OnboardCustomerResponse;
import com.deefrent.rnd.fieldapp.responses.OnboardMerchantAgentResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardAccountRepo {
    private OnboardAccountApiService onboardAccountApiService;

    public OnboardAccountRepo() {
        onboardAccountApiService = ApiClient.Companion.getRetrofit().create(OnboardAccountApiService.class);
    }

    public LiveData<OnboardCustomerResponse> createCustomer(CreateCustomerBody createCustomerBody) {
        MutableLiveData<OnboardCustomerResponse> data = new MutableLiveData<>();
        onboardAccountApiService.onboardCustomer(createCustomerBody).enqueue(new Callback<OnboardCustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardCustomerResponse> call, @NonNull Response<OnboardCustomerResponse> response) {
                data.setValue(response.body());
                /*try {
                    Log.d("Onboard Account Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardCustomerResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Onboard Account Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<OnboardCustomerResponse> onboardIndividualAccount(RequestBody customerdata,
                                                                      MultipartBody.Part frontIdCapture,
                                                                      MultipartBody.Part backIdCapture,
                                                                      MultipartBody.Part passportPhotoCapture) {
        MutableLiveData<OnboardCustomerResponse> data = new MutableLiveData<>();
        onboardAccountApiService.onboardIndividualAccount(customerdata, frontIdCapture, backIdCapture, passportPhotoCapture).enqueue(new Callback<OnboardCustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardCustomerResponse> call, @NonNull Response<OnboardCustomerResponse> response) {
                data.setValue(response.body());
                /*try {
                    Log.d("Onboard Account Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardCustomerResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Onboard Account Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<OnboardMerchantAgentResponse> onboardMerchantAgentAccount(RequestBody merchDetails,
                                                                              MultipartBody.Part termsAndConditionDoc,
                                                                              MultipartBody.Part customerPhoto,
                                                                              MultipartBody.Part signatureDoc,
                                                                              MultipartBody.Part businessPermitDoc,
                                                                              MultipartBody.Part companyRegistrationDoc,
                                                                              MultipartBody.Part frontIDFile,
                                                                              MultipartBody.Part backIDFile,
                                                                              MultipartBody.Part goodConductFile,
                                                                              MultipartBody.Part fieldApplicationFormFile,
                                                                              MultipartBody.Part kraPinFile,
                                                                              MultipartBody.Part businessLicenseFile,
                                                                              MultipartBody.Part shopPhotoFile) {
        MutableLiveData<OnboardMerchantAgentResponse> data = new MutableLiveData<>();
        onboardAccountApiService.onboardMerchantAgentAccount(merchDetails, termsAndConditionDoc, customerPhoto, signatureDoc, businessPermitDoc, companyRegistrationDoc,
                frontIDFile, backIDFile, goodConductFile, fieldApplicationFormFile, kraPinFile, businessLicenseFile,
                shopPhotoFile).enqueue(new Callback<OnboardMerchantAgentResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Response<OnboardMerchantAgentResponse> response) {
                data.setValue(response.body());
                /*try {
                    Log.d("Onboard Account Repo: R", "" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Onboard Account Repo: F", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<OnboardMerchantAgentResponse> createMerchantAgent(CreateMerchantBody createMerchantBody) {
        MutableLiveData<OnboardMerchantAgentResponse> data = new MutableLiveData<>();
        onboardAccountApiService.onboardMerchantAgent(createMerchantBody).enqueue(new Callback<OnboardMerchantAgentResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Response<OnboardMerchantAgentResponse> response) {
                data.setValue(response.body());
                /*try {
                    Gson gson = new Gson();
                    String errorBody = gson.toJson(response.errorBody().string());
                    String body = gson.toJson(response.body());
                    String errorCode = gson.toJson(response.code());
                    Log.d("Auth Repo", "error body: " + errorBody);
                    Log.d("Auth Repo", "body: " + body);
                    Log.d("Auth Repo", "error code: " + errorCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Onboard Account Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public void onboardMerchantAgentAccountWorker(RequestBody merchDetails,
                                                  MultipartBody.Part termsAndConditionDoc,
                                                  MultipartBody.Part customerPhoto,
                                                  MultipartBody.Part signatureDoc,
                                                  MultipartBody.Part businessPermitDoc,
                                                  MultipartBody.Part companyRegistrationDoc,
                                                  MultipartBody.Part frontIDFile,
                                                  MultipartBody.Part backIDFile,
                                                  MultipartBody.Part goodConductFile,
                                                  MultipartBody.Part fieldApplicationFormFile,
                                                  MultipartBody.Part kraPinFile,
                                                  MultipartBody.Part businessLicenseFile,
                                                  MultipartBody.Part shopPhotoFile,
                                                  IExecute<OnboardMerchantAgentResponse> callBack) {
        onboardAccountApiService.onboardMerchantAgentAccount(merchDetails, termsAndConditionDoc, customerPhoto, signatureDoc, businessPermitDoc, companyRegistrationDoc,
                frontIDFile, backIDFile, goodConductFile, fieldApplicationFormFile, kraPinFile, businessLicenseFile,
                shopPhotoFile).enqueue(new Callback<OnboardMerchantAgentResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Response<OnboardMerchantAgentResponse> response) {
                callBack.run(response, null);
                /*try {
                    Log.d("Onboard Account Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardMerchantAgentResponse> call, @NonNull Throwable t) {
                callBack.run(null, t);
                Log.d("Onboard Account Repo", t.getLocalizedMessage());
            }
        });
    }

    public void onboardCustomerAccountWorker(RequestBody customerdata,
                                             MultipartBody.Part frontIdCapture,
                                             MultipartBody.Part backIdCapture,
                                             MultipartBody.Part passportPhotoCapture,
                                             IExecute<OnboardCustomerResponse> callBack) {
        onboardAccountApiService.onboardIndividualAccount(customerdata, frontIdCapture, backIdCapture, passportPhotoCapture).enqueue(new Callback<OnboardCustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<OnboardCustomerResponse> call, @NonNull Response<OnboardCustomerResponse> response) {
                callBack.run(response, null);
                /*try {
                    Log.d("Onboard Account Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<OnboardCustomerResponse> call, @NonNull Throwable t) {
                callBack.run(null, t);
                Log.d("Onboard Account Repo", t.getLocalizedMessage());
            }
        });
    }
}

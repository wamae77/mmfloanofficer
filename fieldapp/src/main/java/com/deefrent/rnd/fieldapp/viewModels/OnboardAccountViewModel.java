package com.deefrent.rnd.fieldapp.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateCustomerBody;
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateMerchantBody;
import com.deefrent.rnd.fieldapp.repositories.OnboardAccountRepo;
import com.deefrent.rnd.fieldapp.responses.OnboardCustomerResponse;
import com.deefrent.rnd.fieldapp.responses.OnboardMerchantAgentResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OnboardAccountViewModel extends ViewModel {
    private OnboardAccountRepo onboardAccountRepo;

    public OnboardAccountViewModel() {
        onboardAccountRepo = new OnboardAccountRepo();
    }

    public LiveData<OnboardCustomerResponse> createCustomer(CreateCustomerBody createCustomerBody) {
        return onboardAccountRepo.createCustomer(createCustomerBody);
    }

    public LiveData<OnboardMerchantAgentResponse> createMerchantAgent(CreateMerchantBody createMerchantBody) {
        return onboardAccountRepo.createMerchantAgent(createMerchantBody);
    }

    public LiveData<OnboardCustomerResponse> onboardIndividualAccount(RequestBody customerdata,
                                                                      MultipartBody.Part frontIdCapture,
                                                                      MultipartBody.Part backIdCapture,
                                                                      MultipartBody.Part passportPhotoCapture) {
        return onboardAccountRepo.onboardIndividualAccount(customerdata, frontIdCapture, backIdCapture, passportPhotoCapture);
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
        return onboardAccountRepo.onboardMerchantAgentAccount(merchDetails, termsAndConditionDoc, customerPhoto, signatureDoc, businessPermitDoc, companyRegistrationDoc,
                frontIDFile, backIDFile, goodConductFile,fieldApplicationFormFile, kraPinFile, businessLicenseFile,
                shopPhotoFile);
    }
}

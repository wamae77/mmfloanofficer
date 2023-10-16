package com.deefrent.rnd.fieldapp.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.deefrent.rnd.fieldapp.R;
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateCustomerBody;
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateMerchantBody;
import com.deefrent.rnd.fieldapp.repositories.OnboardAccountRepo;
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase;
import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails;
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SyncLocalDataWorker extends Worker {
    private final String TAG = "WorkManager";
    private OnboardAccountRepo onboardAccountRepo;
    private FieldAppDatabase fieldAppDatabase;
    private int COUNTER = 0;
    private NotificationChannel channel;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public SyncLocalDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        onboardAccountRepo = new OnboardAccountRepo();
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork: " + Result.success());
        List<MerchantAgentDetails> merchantAgentDetailsList;
        List<IndividualAccountDetails> individualAccountDetailsList;
        fieldAppDatabase = FieldAppDatabase.getFieldAppDatabase(getApplicationContext());
        merchantAgentDetailsList = fieldAppDatabase.merchantAgentDetailsDao().getAllMerchantAgentDetails1();
        if (merchantAgentDetailsList.size() > 0) {
            createMerchantAgentMultiPart(merchantAgentDetailsList);
            displayNotification(getApplicationContext().getResources().getString(R.string.app_name), "Syncing Merchant/ Agent Data");
        }
        individualAccountDetailsList = fieldAppDatabase.individualAccountDetailsDao().getAllIndividualAccountDetails1();
        if (individualAccountDetailsList.size() > 0) {
            createCustomerMultiPart(individualAccountDetailsList);
            displayNotification(getApplicationContext().getResources().getString(R.string.app_name), "Syncing Customer Data");
        }
        Log.e("syncData", "merchantAgentList: " + merchantAgentDetailsList.size());
        Log.e("syncData", "individualAccountList: " + individualAccountDetailsList.size());
        return Result.success();
    }

    private void createMerchantAgentMultiPart(List<MerchantAgentDetails> merchantAgentDetailsList) {
        for (MerchantAgentDetails merchantAgentDetails : merchantAgentDetailsList) {
            int RoomDBId = merchantAgentDetails.getId();
            CreateMerchantBody createMerchantBody = new CreateMerchantBody(merchantAgentDetails.getBusinessName(),
                    merchantAgentDetails.getBusinessMobileNumber(), merchantAgentDetails.getBusinessEmail(),
                    merchantAgentDetails.getBusinessNature(), merchantAgentDetails.getBankCode(),
                    merchantAgentDetails.getBranchCode(), merchantAgentDetails.getAccountName(),
                    merchantAgentDetails.getAccountNumber(), merchantAgentDetails.getCountyCode(),
                    merchantAgentDetails.getTownName(), merchantAgentDetails.getStreetName(), merchantAgentDetails.getBuldingName(),
                    merchantAgentDetails.getRoomNo(), merchantAgentDetails.getMerchAgentAccountTypeId(),
                    merchantAgentDetails.getUserAccountTypeId(), merchantAgentDetails.getBusinessTypeId(),
                    merchantAgentDetails.getLiquidationTypeId(), merchantAgentDetails.getLiquidationRate(),
                    merchantAgentDetails.getLongitude(), merchantAgentDetails.getLatitude(), merchantAgentDetails.getMerchantIDNumber(),
                    merchantAgentDetails.getMerchantSurname(), merchantAgentDetails.getMerchantFirstName(),
                    merchantAgentDetails.getMerchantLastName(), merchantAgentDetails.getDob(), merchantAgentDetails.getMerchantGender());
            Gson gson = new Gson();
            String json = gson.toJson(createMerchantBody);
            RequestBody merchDetails = RequestBody.create(MultipartBody.FORM, json);
            File CompanyRegistrationFile = convertPathToFile(merchantAgentDetails.getCompanyRegistrationDocPath());
            MultipartBody.Part companyRegistrationFile = MultipartBody.Part.createFormData("companyRegistrationDoc", CompanyRegistrationFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), CompanyRegistrationFile));
            //Log.d(TAG, "company reg file: " + companyRegistrationFile);
            File TermsAndConditionFile = convertPathToFile(merchantAgentDetails.getTermsAndConditionDocPath());
            MultipartBody.Part termsAndConditionFile = MultipartBody.Part.createFormData("termsAndConditionDoc",
                    TermsAndConditionFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            TermsAndConditionFile));
            File CustomerPhotoFile = convertPathToFile(merchantAgentDetails.getCustomerPhotoPath());
            MultipartBody.Part customerPhotoFile = MultipartBody.Part.createFormData("customerPhoto",
                    CustomerPhotoFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            CustomerPhotoFile));
            File SignatureDocFile = convertPathToFile(merchantAgentDetails.getSignatureDocPath());
            MultipartBody.Part signatureDocFile = MultipartBody.Part.createFormData("signatureDoc",
                    SignatureDocFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            SignatureDocFile));
            File BusinessPermitDocFile = convertPathToFile(merchantAgentDetails.getBusinessPermitDocPath());
            MultipartBody.Part businessPermitDocFile = MultipartBody.Part.createFormData("businessPermitDoc",
                    BusinessPermitDocFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            BusinessPermitDocFile));
            File FrontIDFile = convertPathToFile(merchantAgentDetails.getFrontIdPath());
            MultipartBody.Part frontIDFile = MultipartBody.Part.createFormData("frontID",
                    FrontIDFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            FrontIDFile));
            File BackIDFile = convertPathToFile(merchantAgentDetails.getBackIdPath());
            MultipartBody.Part backIDFile = MultipartBody.Part.createFormData("backID",
                    BackIDFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            BackIDFile));
            File GoodConductFile = convertPathToFile(merchantAgentDetails.getGoodConductPath());
            MultipartBody.Part goodConductFile = MultipartBody.Part.createFormData("certificateOFGoodConduct",
                    GoodConductFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            GoodConductFile));
            File FieldApplicationFormFile = convertPathToFile(merchantAgentDetails.getFieldApplicationFormPath());
            MultipartBody.Part fieldApplicationFormFile = MultipartBody.Part.createFormData("fieldApplicationForm",
                    FieldApplicationFormFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            FieldApplicationFormFile));
            File KRAPinFile = convertPathToFile(merchantAgentDetails.getKraPINPath());
            MultipartBody.Part kraPinFile = MultipartBody.Part.createFormData("kraPinCertificate",
                    KRAPinFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            KRAPinFile));
            File BusinessLicenseFile = convertPathToFile(merchantAgentDetails.getBusinessLicensePath());
            MultipartBody.Part businessLicenseFile = MultipartBody.Part.createFormData("businessLicense",
                    BusinessLicenseFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            BusinessLicenseFile));
            File ShopPhotoFile = convertPathToFile(merchantAgentDetails.getShopPhotoPath());
            MultipartBody.Part shopPhotoFile = MultipartBody.Part.createFormData("shopPhoto",
                    ShopPhotoFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            ShopPhotoFile));
            onboardMerchantAgentAccount1(merchDetails, termsAndConditionFile, customerPhotoFile, signatureDocFile,
                    businessPermitDocFile, companyRegistrationFile, frontIDFile, backIDFile, goodConductFile,
                    fieldApplicationFormFile, kraPinFile, businessLicenseFile, shopPhotoFile, RoomDBId);
        }
    }

    private void createCustomerMultiPart(List<IndividualAccountDetails> individualAccountDetailsList) {
        for (IndividualAccountDetails individualAccountDetails : individualAccountDetailsList) {
            int RoomDBId = individualAccountDetails.getId();
            CreateCustomerBody createCustomerBody = new CreateCustomerBody(individualAccountDetails.getUserAccountTypeId(),
                    individualAccountDetails.getPhoneNo(), individualAccountDetails.getPersonalAccountTypeId(),
                    individualAccountDetails.getKCBBranchId(), individualAccountDetails.getIdNumber(),
                    individualAccountDetails.getSurname(), individualAccountDetails.getFirstName(),
                    individualAccountDetails.getLastName(), individualAccountDetails.getSysUserId(),
                    individualAccountDetails.getDob(), individualAccountDetails.getGender(),
                    30000, individualAccountDetails.getWorkLocation(),
                    individualAccountDetails.getEmploymentType(), individualAccountDetails.getAccountOpeningPurpose(),
                    individualAccountDetails.getLongitude(), individualAccountDetails.getLatitude());
            Gson gson = new Gson();
            String json = gson.toJson(createCustomerBody);
            RequestBody customerData = RequestBody.create(MultipartBody.FORM, json);
            File PassportPhotoFile = convertPathToFile(individualAccountDetails.getPassportPhotoPath());
            MultipartBody.Part passportPhotoFile = MultipartBody.Part.createFormData("passportPhotoCapture",
                    PassportPhotoFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            PassportPhotoFile));
            File FrontIDFile = convertPathToFile(individualAccountDetails.getFrontIdPath());
            MultipartBody.Part frontIDFile = MultipartBody.Part.createFormData("frontIdCapture",
                    FrontIDFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            FrontIDFile));
            File BackIDFile = convertPathToFile(individualAccountDetails.getBackIdPath());
            MultipartBody.Part backIDFile = MultipartBody.Part.createFormData("backIdCapture",
                    BackIDFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                            BackIDFile));
            onboardIndividualAccount(customerData, frontIDFile, backIDFile, passportPhotoFile,
                    RoomDBId);
        }
    }

    private void onboardIndividualAccount(RequestBody customerdata, MultipartBody.Part frontIdCapture,
                                          MultipartBody.Part backIdCapture, MultipartBody.Part passportPhotoCapture,
                                          int RoomDBID) {
        onboardAccountRepo.onboardCustomerAccountWorker(customerdata, frontIdCapture, backIdCapture, passportPhotoCapture, (result, t) -> {
            if (result != null && result.body() != null) {
                Log.d(TAG, "onboardCustomerAccount: success");
                deleteCustomerRecordLocally(RoomDBID);
            } else {
                Log.d(TAG, "onboardCustomerAccount: failed");
            }
        });
    }

    private void onboardMerchantAgentAccount1(RequestBody merchDetails, MultipartBody.Part termsAndConditionDoc,
                                              MultipartBody.Part customerPhoto, MultipartBody.Part signatureDoc, MultipartBody.Part businessPermitDoc,
                                              MultipartBody.Part companyRegistrationDoc, MultipartBody.Part frontIDFile, MultipartBody.Part backIDFile,
                                              MultipartBody.Part goodConductFile, MultipartBody.Part fieldApplicationFormFile, MultipartBody.Part kraPinFile,
                                              MultipartBody.Part businessLicenseFile, MultipartBody.Part shopPhotoFile, int RoomDBID
    ) {
        onboardAccountRepo.onboardMerchantAgentAccountWorker(merchDetails, termsAndConditionDoc, customerPhoto, signatureDoc,
                businessPermitDoc, companyRegistrationDoc, frontIDFile, backIDFile, goodConductFile,
                fieldApplicationFormFile, kraPinFile, businessLicenseFile, shopPhotoFile, (result, t) -> {
                    if (result != null && result.body() != null) {
                        Log.d(TAG, "onboardMerchantAgentAccount1: success");
                        deleteMerchantRecordLocally(RoomDBID);
                    } else {
                        Log.d(TAG, "onboardMerchantAgentAccount1: failed");
                    }
                });
    }

    private void deleteMerchantRecordLocally(int RoomDBID) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(fieldAppDatabase.merchantAgentDetailsDao().deleteMerchantAgentRecord(RoomDBID)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(compositeDisposable::dispose));
    }

    private void deleteCustomerRecordLocally(int RoomDBID) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(fieldAppDatabase.individualAccountDetailsDao().deleteCustomerRecord(RoomDBID)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(compositeDisposable::dispose));
    }

    private File convertPathToFile(String imagePath) {
        return new File(imagePath);
    }

    private void displayNotification(String title, String task) {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("Field Agent App", getApplicationContext().getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "Field Agent App")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.app_icon3)
                .setAutoCancel(false)
                .setProgress(10, 3, false);

        notificationManager.notify(1, notificationBuilder.build());
    }
}

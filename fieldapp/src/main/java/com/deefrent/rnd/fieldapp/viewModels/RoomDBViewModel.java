package com.deefrent.rnd.fieldapp.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase;
import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails;
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class RoomDBViewModel extends AndroidViewModel {
    private FieldAppDatabase fieldAppDatabase;

    public RoomDBViewModel(@NonNull Application application) {
        super(application);
        fieldAppDatabase = FieldAppDatabase.getFieldAppDatabase(application);
    }

    public Single<Long> addToIndividualAccountDetails(IndividualAccountDetails individualAccountDetails) {
        return fieldAppDatabase.individualAccountDetailsDao().addIndividualAccountDetail(individualAccountDetails);
    }

    public Flowable<List<IndividualAccountDetails>> loadAllIndividualAccountDetails() {
        return fieldAppDatabase.individualAccountDetailsDao().getAllIndividualAccountDetails();
    }

    public Single<Long> addToMerchantAgentDetails(MerchantAgentDetails merchantAgentDetails) {
        return fieldAppDatabase.merchantAgentDetailsDao().addMerchantAgentDetail(merchantAgentDetails);
    }

    public Completable updateLiquidationDetails(int newLiquidationTypeId, int newLiquidationRate, String newBankCode, String newBranchCode,
                                                String newAccountName, String newAccountNumber, String newLastStep, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateLiquidationDetails(newLiquidationTypeId, newLiquidationRate, newBankCode, newBranchCode,
                newAccountName, newAccountNumber, newLastStep, id);
    }

    public Completable updatePhysicalAddressDetails(String newCountyCode, String newTown, String newStreetName,
                                                    String newBuildingName, String newRoomNo, String newLatitude, String newLongitude, String newLastStep, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updatePhysicalAddressDetails(newCountyCode, newTown, newStreetName,
                newBuildingName, newRoomNo, newLatitude, newLongitude, newLastStep, id);
    }

    public Completable updateMerchantDetails(String userType, int userAccountTypeId, int merchAgentAccountTypeId, String businessName,
                                             String businessMobileNumber, String businessEmail, int businessTypeId, String businessNature, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateMerchantDetails(userType, userAccountTypeId, merchAgentAccountTypeId, businessName,
                businessMobileNumber, businessEmail, businessTypeId, businessNature, id);
    }

    public Completable updateMerchantPersonalDetails(String merchantIDNumber, String merchantSurname, String merchantFirstName,
                                                     String merchantLastName, String dob, String merchantGender, String newLastStep,String idType, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateMerchantPersonalDetails(merchantIDNumber, merchantSurname, merchantFirstName,
                merchantLastName, dob, merchantGender, newLastStep,idType, id);
    }

    public Completable updateMerchantIDDetails(String frontIdPath, String backIdPath, String newLastStep, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateMerchantIDDetails(frontIdPath, backIdPath, newLastStep, id);
    }

    public Completable updateMerchantPersonalImages(String customerPhotoPath, String signatureDocPath, String goodConductPath,
                                                    String fieldApplicationFormPath, String newLastStep, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateMerchantPersonalImages(customerPhotoPath, signatureDocPath, goodConductPath,
                fieldApplicationFormPath, newLastStep, id);
    }

    public Completable updateMerchantBusinessImages(String termsAndConditionDocPath, String kraPINPath, String businessLicensePath,
                                                    String businessPermitDocPath, String companyRegistrationDocPath, String newLastStep,
                                                    boolean complete, String shopPhotoPath, int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().updateMerchantBusinessImages(termsAndConditionDocPath, kraPINPath, businessLicensePath,
                businessPermitDocPath, companyRegistrationDocPath, newLastStep, complete, shopPhotoPath, id);
    }

    public Completable deleteMerchantAgentRecord(int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().deleteMerchantAgentRecord(id);
    }

    public Completable updateCustomerDetails(int personalAccountTypeId, int userAccountTypeId, int KCBBranchId, String phoneNo,
                                             String surname, String firstName, String lastName, String gender, String dob,
                                             String idNumber, String idType, int id) {
        return fieldAppDatabase.individualAccountDetailsDao().updateCustomerDetails(personalAccountTypeId, userAccountTypeId, KCBBranchId, phoneNo,
                surname, firstName, lastName, gender, dob, idNumber, idType, id);
    }

    public Completable updateCustomerIDDetails(String frontIdPath, String backIdPath, String newLastStep, int id) {
        return fieldAppDatabase.individualAccountDetailsDao().updateCustomerIDDetails(frontIdPath, backIdPath, newLastStep, id);
    }

    public Completable updateCustomerPassportPhoto(String passportPhotoPath, String newLastStep, int id) {
        return fieldAppDatabase.individualAccountDetailsDao().updateCustomerPassportPhoto(passportPhotoPath,
                newLastStep, id);
    }

    public Completable updateCustomerWorkDetails(int employmentType, String workLocation, String accountOpeningPurpose,
                                                 String income, String latitude, String longitude, String newLastStep, boolean complete, int id) {
        return fieldAppDatabase.individualAccountDetailsDao().updateCustomerWorkDetails(employmentType, workLocation,
                accountOpeningPurpose, income, latitude, longitude, newLastStep, complete, id);
    }

    public Completable deleteCustomerRecord(int id) {
        return fieldAppDatabase.individualAccountDetailsDao().deleteCustomerRecord(id);
    }

    public Flowable<List<IndividualAccountDetails>> fetchIncompleteIndividualAccounts() {
        return fieldAppDatabase.individualAccountDetailsDao().getIncompleteIndividualAccountDetails();
    }

    public Flowable<List<MerchantAgentDetails>> fetchIncompleteMerchantAgentAccounts() {
        return fieldAppDatabase.merchantAgentDetailsDao().getIncompleteMerchantAgentDetails();
    }

    public Flowable<IndividualAccountDetails> fetchIncompleteIndividualAccount(int id) {
        return fieldAppDatabase.individualAccountDetailsDao().getIncompleteIndividualAccount(id);
    }

    public Flowable<MerchantAgentDetails> fetchIncompleteMerchantAgentAccount(int id) {
        return fieldAppDatabase.merchantAgentDetailsDao().getIncompleteMerchantAgent(id);
    }
}

package com.deefrent.rnd.fieldapp.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MerchantAgentDetailsDao {
    @Query("SELECT * FROM merchant_agent_details")
    Flowable<List<MerchantAgentDetails>> getAllMerchantAgentDetails();

    @Query("SELECT * FROM merchant_agent_details WHERE complete=1")
    List<MerchantAgentDetails> getAllMerchantAgentDetails1();

    @Query("SELECT * FROM merchant_agent_details WHERE complete=0 ORDER BY id DESC")
    Flowable<List<MerchantAgentDetails>> getIncompleteMerchantAgentDetails();

    @Query("SELECT * FROM merchant_agent_details WHERE id=:id")
    Flowable<MerchantAgentDetails> getIncompleteMerchantAgent(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> addMerchantAgentDetail(MerchantAgentDetails merchantAgentDetails);

    @Delete
    Completable removeMerchantAgentDetails(MerchantAgentDetails merchantAgentDetails);

    @Query("SELECT * FROM merchant_agent_details WHERE id=:merchantAgentDetailId")
    Flowable<MerchantAgentDetails> getSingleMerchantAgentDetail(int merchantAgentDetailId);

    @Query("UPDATE merchant_agent_details SET userType=:userType, " +
            "userAccountTypeId=:userAccountTypeId,merchAgentAccountTypeId=:merchAgentAccountTypeId," +
            "businessName=:businessName, businessMobileNumber=:businessMobileNumber,businessEmail=:businessEmail," +
            "businessTypeId=:businessTypeId,businessNature=:businessNature WHERE id=:id")
    Completable updateMerchantDetails(String userType, int userAccountTypeId, int merchAgentAccountTypeId, String businessName,
                                      String businessMobileNumber, String businessEmail, int businessTypeId, String businessNature, int id);

    @Query("UPDATE merchant_agent_details SET liquidationTypeId=:newLiquidationTypeId, " +
            "liquidationRate=:newLiquidationRate,bankCode=:newBankCode," +
            "branchCode=:newBranchCode, accountName=:newAccountName,accountNumber=:newAccountNumber," +
            "lastStep=:newLastStep WHERE id=:id")
    Completable updateLiquidationDetails(int newLiquidationTypeId, int newLiquidationRate, String newBankCode, String newBranchCode,
                                         String newAccountName, String newAccountNumber, String newLastStep, int id);

    @Query("UPDATE merchant_agent_details SET countyCode=:newCountyCode, " +
            "townName=:newTown,streetName=:newStreetName," +
            "buldingName=:newBuildingName, roomNo=:newRoomNo,latitude=:newLatitude,longitude=:newLongitude," +
            "lastStep=:newLastStep WHERE id=:id")
    Completable updatePhysicalAddressDetails(String newCountyCode, String newTown, String newStreetName,
                                             String newBuildingName, String newRoomNo, String newLatitude, String newLongitude,
                                             String newLastStep, int id);

    @Query("UPDATE merchant_agent_details SET merchantIDNumber=:merchantIDNumber, " +
            "merchantSurname=:merchantSurname,merchantFirstName=:merchantFirstName," +
            "merchantLastName=:merchantLastName, dob=:dob,merchantGender=:merchantGender,lastStep=:newLastStep,idType=:idType WHERE id=:id")
    Completable updateMerchantPersonalDetails(String merchantIDNumber, String merchantSurname, String merchantFirstName,
                                              String merchantLastName, String dob, String merchantGender, String newLastStep, String idType, int id);

    @Query("UPDATE merchant_agent_details SET frontIdPath=:frontIdPath, " +
            "backIdPath=:backIdPath,lastStep=:newLastStep WHERE id=:id")
    Completable updateMerchantIDDetails(String frontIdPath, String backIdPath, String newLastStep, int id);

    @Query("UPDATE merchant_agent_details SET customerPhotoPath=:customerPhotoPath, " +
            "signatureDocPath=:signatureDocPath,goodConductPath= :goodConductPath,fieldApplicationFormPath=:fieldApplicationFormPath," +
            "lastStep=:newLastStep WHERE id=:id")
    Completable updateMerchantPersonalImages(String customerPhotoPath, String signatureDocPath, String goodConductPath,
                                             String fieldApplicationFormPath, String newLastStep, int id);

    @Query("UPDATE merchant_agent_details SET termsAndConditionDocPath=:termsAndConditionDocPath, " +
            "kraPINPath=:kraPINPath,businessLicensePath= :businessLicensePath,businessPermitDocPath=:businessPermitDocPath,companyRegistrationDocPath=:companyRegistrationDocPath, " +
            "lastStep=:newLastStep,complete=:complete,shopPhotoPath=:shopPhotoPath WHERE id=:id")
    Completable updateMerchantBusinessImages(String termsAndConditionDocPath, String kraPINPath, String businessLicensePath,
                                             String businessPermitDocPath, String companyRegistrationDocPath, String newLastStep,
                                             boolean complete, String shopPhotoPath, int id);

    @Query("SELECT COUNT (*) from merchant_agent_details WHERE complete=1")
    Single<Integer> countMerchantAgentDetails();

    @Query("SELECT COUNT (*) from merchant_agent_details WHERE complete=0")
    Single<Integer> countIncompleteMerchantAgentDetails();

    @Query("DELETE FROM merchant_agent_details WHERE id=:id ")
    Completable deleteMerchantAgentRecord(int id);
}

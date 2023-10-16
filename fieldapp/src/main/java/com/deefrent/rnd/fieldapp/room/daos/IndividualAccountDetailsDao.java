package com.deefrent.rnd.fieldapp.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface IndividualAccountDetailsDao {
    @Query("SELECT * FROM individual_account_details")
    Flowable<List<IndividualAccountDetails>> getAllIndividualAccountDetails();

    @Query("SELECT * FROM individual_account_details WHERE complete=1")
    List<IndividualAccountDetails> getAllIndividualAccountDetails1();

    @Query("SELECT * FROM individual_account_details WHERE complete=0 ORDER BY id DESC")
    Flowable<List<IndividualAccountDetails>> getIncompleteIndividualAccountDetails();

    @Query("SELECT * FROM individual_account_details WHERE id=:id")
    Flowable<IndividualAccountDetails> getIncompleteIndividualAccount(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> addIndividualAccountDetail(IndividualAccountDetails individualAccountDetails);

    @Delete
    Completable removeIndividualAccountDetails(IndividualAccountDetails individualAccountDetails);

    @Query("SELECT * FROM individual_account_details WHERE id=:individualAccountDetailId")
    Flowable<IndividualAccountDetails> getSingleIndividualAccountDetail(String individualAccountDetailId);

    @Query("UPDATE individual_account_details SET personalAccountTypeId=:personalAccountTypeId, " +
            "userAccountTypeId=:userAccountTypeId,KCBBranchId=:KCBBranchId,phoneNo=:phoneNo," +
            "surname=:surname,firstName=:firstName,lastName=:lastName,gender=:gender,dob=:dob,idNumber=:idNumber," +
            "idType=:idType WHERE id=:id")
    Completable updateCustomerDetails(int personalAccountTypeId, int userAccountTypeId, int KCBBranchId, String phoneNo,
                                      String surname, String firstName, String lastName, String gender, String dob,
                                      String idNumber, String idType, int id);

    @Query("UPDATE individual_account_details SET frontIdPath=:frontIdPath, " +
            "backIdPath=:backIdPath,lastStep=:newLastStep WHERE id=:id")
    Completable updateCustomerIDDetails(String frontIdPath, String backIdPath, String newLastStep, int id);

    @Query("UPDATE individual_account_details SET passportPhotoPath=:passportPhotoPath, " +
            "lastStep=:newLastStep WHERE id=:id")
    Completable updateCustomerPassportPhoto(String passportPhotoPath, String newLastStep, int id);

    @Query("UPDATE individual_account_details SET employmentType=:employmentType, workLocation=:workLocation," +
            "accountOpeningPurpose=:accountOpeningPurpose,income=:income," +
            "latitude=:latitude,longitude=:longitude,lastStep=:newLastStep,complete=:complete WHERE id=:id")
    Completable updateCustomerWorkDetails(int employmentType, String workLocation, String accountOpeningPurpose, String income,
                                          String latitude, String longitude, String newLastStep, boolean complete, int id);

    @Query("DELETE FROM individual_account_details WHERE id=:id ")
    Completable deleteCustomerRecord(int id);

    @Query("SELECT COUNT (*) from individual_account_details WHERE complete=1")
    Single<Integer> countCustomerDetails();

    @Query("SELECT COUNT (*) from individual_account_details WHERE complete=0")
    Single<Integer> countIncompleteCustomerDetails();
}

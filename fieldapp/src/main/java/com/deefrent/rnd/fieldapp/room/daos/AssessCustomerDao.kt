package com.deefrent.rnd.fieldapp.room.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.deefrent.rnd.fieldapp.room.entities.*
import com.google.gson.Gson

@Dao
interface AssessCustomerDao {
    /***/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssessedCustomer(assessCustomerEntity: AssessCustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollaterals(coll: List<AssessCollateral>)

    @Query("DELETE from AssessCollateral")
    fun deleteCollateral(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGuarantors(gua: List<AssessGuarantor>)

    @Query("DELETE from AssessGuarantor")
    fun deleteGuarantors(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBorrowings(borrow: List<AssessBorrowing>)

    @Query("DELETE from AssessBorrowing")
    fun deleteBorrowing(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouseholds(household: List<AssessHouseholdMemberEntity>)

    @Query("DELETE from AssessHouseholdMemberEntity")
    fun deleteHouseholdMember(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerDocs(customerDocs: List<AssessCustomerDocsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleCollateral(collateral: AssessCollateral)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleGuarantor(guarantor: AssessGuarantor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleDocs(customerDocs: AssessCustomerDocsEntity)

    @Query("DELETE from AssessCustomerDocsEntity")
    fun deleteCustomerDocs(): Int

    @Delete
    fun deleteAssessedCustomer(assessCustomerEntity: AssessCustomerEntity)

    @Transaction
    fun insertAssessedCustomerTransaction(
        assessCustomerEntity: AssessCustomerEntity,
        custDoc: List<AssessCustomerDocsEntity>,
        coll: List<AssessCollateral>,
        gua: List<AssessGuarantor>,
        borrow: List<AssessBorrowing>,
        household: List<AssessHouseholdMemberEntity>
    ) {
        //deleteCustomerDocs()
        deleteCollateral()
        deleteGuarantors()
        deleteBorrowing()
        deleteHouseholdMember()
        insertAssessedCustomer(assessCustomerEntity)
        insertCustomerDocs(custDoc)
        insertCollaterals(coll)
        insertGuarantors(gua)
        insertBorrowings(borrow)
        insertHouseholds(household)
        Log.i(
            "TAG",
            "insertAssessedCustomerTransaction:${Gson().toJson(assessCustomerEntity.assessmentPercentage)} "
        )
        Log.i("TAG", "insertAssessedCustomerTransactions:${Gson().toJson(assessCustomerEntity)} ")
    }

    @Query("SELECT * from AssessCustomerEntity WHERE isComplete =:isComplete")
    fun getIncompleteAssessedCustomer(isComplete: Boolean): List<AssessCustomerEntityWithList>

    @Query("SELECT * from AssessCustomerEntity WHERE hasFinished =:hasFinished")
    fun getOfflineAssessedCustomer(hasFinished: Boolean): List<AssessCustomerEntityWithList>

    //@Transaction
    @Query("SELECT * from AssessCustomerEntity where idNumber =:nationalId")
    fun getCustomerDetailsEntityWithListById(nationalId: String): LiveData<AssessCustomerEntityWithList>

    @Update
    fun updateCollateral(collateral: AssessCollateral)

    @Update
    fun updateCustomerDoc(customerDoc: AssessCustomerDocsEntity)

    @Transaction
    fun updateCollateralAndDoc(
        collateral: AssessCollateral,
        customerDoc: AssessCustomerDocsEntity
    ) {
        updateCollateral(collateral)
        updateCustomerDoc(customerDoc)
    }

    @Query("DELETE from AssessCollateral Where id=:id")
    suspend fun deleteCollWithID(id: Int)

    @Query("DELETE from AssessCustomerDocsEntity Where docGeneratedUID=:docGeneratedUID")
    suspend fun deleteCollateralDoc(docGeneratedUID: String)

    @Transaction
    suspend fun deleteAssessedCollateral(collateralID: Int, docGeneratedUID: String) {
        deleteCollWithID(collateralID)
        deleteCollateralDoc(docGeneratedUID)
    }

    @Update
    fun updateGuarantor(guarantor: AssessGuarantor)

    @Query("update AssessCustomerDocsEntity set docPath=:docPath Where id=:id")
    fun updateCustomerDocuments(id: Int, docPath: String)

    @Transaction
    fun updateGuarantorAndDoc(
        guarantor: AssessGuarantor,
        customerDocs: List<AssessCustomerDocsEntity>
    ) {
        updateGuarantor(guarantor)
        Log.d("TAG", "updateGuarantorAndDoc:${Gson().toJson(customerDocs)} ")
        //updateCustomerDocument(customerDocs)
        customerDocs.forEach { entity ->
            updateCustomerDocuments(entity.id, entity.docPath)
            Log.d("TAG", "updateGuarantorAndDocafter:${Gson().toJson(customerDocs)} ")

        }

    }

    @Query("DELETE from AssessGuarantor Where id=:id")
    suspend fun deleteGuarWithID(id: Int)

    @Query("DELETE from AssessCustomerDocsEntity Where parentIdNumber=:idNumber and docGeneratedUID=:docGeneratedUID ")
    suspend fun deleteGuarantorCustomerDocs(idNumber: String, docGeneratedUID: String)

    @Transaction
    suspend fun deleteGuarantor(id: Int, parentID: String, docGeneratedUID: String) {
        deleteGuarantorCustomerDocs(parentID, docGeneratedUID)
        deleteGuarWithID(id)
    }

    @Query("SELECT * FROM AssessCustomerDocsEntity Where parentIdNumber==:parentID")
    fun getCustomerDocs(parentID: String): List<AssessCustomerDocsEntity>

    @Update
    fun updateBorrowing(borrow: AssessBorrowing)

    @Update
    fun updateHMemberWithID(householdMemberEntity: AssessHouseholdMemberEntity)

    @Query("DELETE from AssessBorrowing Where id=:id")
    suspend fun deleteBorrowWithID(id: Int)

    @Query("DELETE from AssessHouseholdMemberEntity WHERE id=:id")
    suspend fun deleteHMemberWithID(id: Int): Int

    @Query("update AssessCustomerEntity set assessmentRemarks=:assessmentRemarks, isComplete=:isComplete, lastStep=:lastStep Where idNumber=:id")
    fun updateCustomerLastStep(
        assessmentRemarks: String,
        isComplete: Boolean,
        id: String,
        lastStep: String
    )

    @Query("update AssessCustomerEntity set isProcessed=:isProcessed Where idNumber=:id")
    fun updateCustomerIsProcessed(isProcessed: Boolean, id: String)

    @Query("update AssessCustomerEntity set hasFinished=:hasFinished Where idNumber=:id")
    fun updateCustomerhasFinished(hasFinished: Boolean, id: String)


}
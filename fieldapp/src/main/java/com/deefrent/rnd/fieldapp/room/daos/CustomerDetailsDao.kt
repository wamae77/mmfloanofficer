package com.deefrent.rnd.fieldapp.room.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.deefrent.rnd.fieldapp.room.entities.*
import com.google.gson.Gson

@Dao
interface CustomerDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomer(customerDetailsEntity: CustomerDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerDetails(customerDetailsEntity: CustomerDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGuarantor(guarantor: List<Guarantor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleGuarantor(guarantor: Guarantor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollateral(collateral: List<Collateral>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleCollateral(collateral: Collateral)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBorrowing(otherBorrowing: List<OtherBorrowing>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouseholds(household: List<HouseholdMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerDocs(customerDocs: List<CustomerDocsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleDocs(customerDocs: CustomerDocsEntity)

    @Query("DELETE from HouseholdMemberEntity")
    fun deleteHouseholdMember(): Int

    @Query("DELETE from Collateral")
    fun deleteCollateral(): Int

    /**collateral*/
    @Query("Select * from Collateral Where parentNationalIdentity=:parentID")
    fun getCollByParentID(parentID: String): LiveData<List<Collateral>>

    @Update
    fun updateCollateral(collateral: Collateral)

    @Update
    fun updateCustomerDoc(customerDoc: CustomerDocsEntity)

    @Update
    fun updateCustomerDocument(customerDoc: List<CustomerDocsEntity>)

    @Query("update CustomerDocsEntity set docPath=:docPath Where id=:id")
    fun updateCustomerDocuments(id: Int, docPath: String)

    @Transaction
    fun updateCollateralAndDoc(collateral: Collateral, customerDoc: CustomerDocsEntity) {
        updateCollateral(collateral)
        updateCustomerDoc(customerDoc)
    }


    @Query("DELETE from Collateral Where id=:id")
    suspend fun deleteCollWithID(id: Int)

    @Query("DELETE from CustomerDocsEntity Where docGeneratedUID=:docGeneratedUID")
    suspend fun deleteCollateralDoc(docGeneratedUID: String)

    /**update and del guarantors*/
    @Query("Select * from Guarantor Where parentNationalIdentity=:parentID")
    fun getGuarByParentID(parentID: String): LiveData<List<Guarantor>>

    @Update
    fun updateGuarantor(guarantor: Guarantor)

    @Transaction
    fun updateGuarantorAndDoc(guarantor: Guarantor, customerDocs: List<CustomerDocsEntity>) {
        updateGuarantor(guarantor)
        Log.d("TAG", "updateGuarantorAndDoc:${Gson().toJson(customerDocs)} ")
        //updateCustomerDocument(customerDocs)
        customerDocs.forEach { entity ->
            updateCustomerDocuments(entity.id, entity.docPath)
            Log.d("TAG", "updateGuarantorAndDocafter:${Gson().toJson(customerDocs)} ")
        }

    }

    @Transaction
    suspend fun deleteGuarantor(id: Int, parentID: String, docGeneratedUID: String) {
        deleteGuarantorCustomerDocs(parentID, docGeneratedUID)
        deleteGuarWithID(id)
    }

    @Query("DELETE from Guarantor Where id=:id")
    suspend fun deleteGuarWithID(id: Int)

    @Query("DELETE from CustomerDocsEntity Where parentNationalIdentity=:idNumber and docGeneratedUID=:docGeneratedUID ")
    suspend fun deleteGuarantorCustomerDocs(idNumber: String, docGeneratedUID: String)

    /**update and del borrowers*/
    @Query("Select * from OtherBorrowing Where parentNationalIdentity=:parentID")
    fun getBorrowingByParentID(parentID: String): LiveData<List<OtherBorrowing>>

    @Update
    fun updateBorrowing(borrow: OtherBorrowing)

    @Update
    fun updateHMemberWithID(householdMemberEntity: HouseholdMemberEntity)

    @Query("Select * from HouseholdMemberEntity Where parentNationalIdentity=:parentID")
    fun getHMemberByParentID(parentID: String): LiveData<List<HouseholdMemberEntity>>

    @Query("SELECT * FROM CustomerDocsEntity Where parentNationalIdentity==:parentID")
    fun getCustomerDocs(parentID: String): List<CustomerDocsEntity>

    @Query("DELETE from OtherBorrowing Where id=:id")
    suspend fun deleteBorrowWithID(id: Int)

    @Query("DELETE from OtherBorrowing")
    fun deleteBorrowing(): Int

    @Query("DELETE from OtherBorrowing WHERE parentNationalIdentity=:parentNationalIdentity")
    fun deleteBorrowingWithID(parentNationalIdentity: String): Int

    @Query("DELETE from Collateral WHERE parentNationalIdentity=:parentNationalIdentity")
    fun deleteCollateralWithID(parentNationalIdentity: String): Int

    @Query("DELETE from Guarantor WHERE parentNationalIdentity=:parentNationalIdentity")
    fun deleteGuarantorWithID(parentNationalIdentity: String): Int

    @Query("DELETE from HouseholdMemberEntity WHERE id=:id")
    suspend fun deleteHMemberWithID(id: Int): Int

    @Transaction
    @Query("DELETE from CustomerDetailsEntity WHERE nationalIdentity=:nationalIdentity")
    fun deleteCustomerDetailsEntityWithID(nationalIdentity: String): Int

    @Delete
    fun deleteCustomer(customerDetailsEntity: CustomerDetailsEntity)

    @Query("DELETE from CustomerDocsEntity")
    fun deleteCustomerDocs(): Int

    /**we have not used this one, instead we use @Delete anotation fun above*/
    @Transaction
    fun deleteCustomerItemsById(nationalIdentity: String) {
        deleteBorrowingWithID(nationalIdentity)
        deleteGuarantorWithID(nationalIdentity)
        deleteCollateralWithID(nationalIdentity)
        deleteCustomerDetailsEntityWithID(nationalIdentity)
    }

    @Transaction
    suspend fun deleteCollateral(collateralID: Int, docGeneratedUID: String) {
        deleteCollWithID(collateralID)
        deleteCollateralDoc(docGeneratedUID)
    }


    @Transaction
    suspend fun insertCustomerDetailsTransaction(
        customerDetailsEntity: CustomerDetailsEntity,
        guarantor: List<Guarantor>,
        collateral: List<Collateral>,
        otherBorrowing: List<OtherBorrowing>,
        household: List<HouseholdMemberEntity>,
        customerDocs: List<CustomerDocsEntity>
    ) {
        Log.d("TAG", "insertCustomerDetailsTransaction: ${customerDetailsEntity.nationalIdentity}")
        //Log.d("TAG", "insertCustomerDetailsTransaction: ${customerDocs[0].parentNationalIdentity}")
        Log.d("TAG", "insertCustomerDetailsTransaction: ${Gson().toJson(customerDetailsEntity)}")
        Log.d("TAG", "insertCustomerDetailsTransaction: ${Gson().toJson(customerDocs)}")
        /**we delete items with no primary key before inserting a new one*/
        //deleteCustomerDocs()
        deleteCollateral()
        deleteBorrowing()
        deleteHouseholdMember()
        insertCustomerDetails(customerDetailsEntity)
        insertCollateral(collateral)
        insertGuarantor(guarantor)
        insertBorrowing(otherBorrowing)
        insertHouseholds(household)
        insertCustomerDocs(customerDocs)
    }

    /**fetch items(not used)*/
    @Transaction
    @Query("SELECT * from CustomerDetailsEntity where nationalIdentity =:nationalId")
    fun getCustomerDetailsEntityWithListById(nationalId: String): LiveData<CusomerDetailsEntityWithList>

    @Transaction
    @Query("SELECT * from CustomerDetailsEntity where nationalIdentity =:nationalId")
    fun fetchDetailsEntityWithListById(nationalId: String): CusomerDetailsEntityWithList?

    /**used on dashboard*/
    @Query("SELECT * from CustomerDetailsEntity WHERE isComplete =:isComplete")
    fun getAllIncompleteCustomerList(isComplete: Boolean): List<CusomerDetailsEntityWithList>

    @Query("SELECT * from CustomerDetailsEntity WHERE hasFinished =:hasFinished")
    fun getAllCompleteOfflineCustomerList(hasFinished: Boolean): List<CusomerDetailsEntityWithList>

    @Query("Select * from CustomerDocsEntity Where parentNationalIdentity=:parentID")
    fun getDocumentsByParentID(parentID: String): LiveData<CustomerDocsEntity>

    @Query("update CustomerDetailsEntity set isComplete=:isComplete, lastStep=:lastStep Where nationalIdentity=:id")
    fun updateCustomerLastStep(isComplete: Boolean, id: String, lastStep: String)

    @Query("update CustomerDetailsEntity set isProcessed=:isProcessed, customerId=:customerId Where nationalIdentity=:id")
    fun updateCustomerIsProcessed(isProcessed: Boolean, id: String, customerId: String)

    @Query("update CustomerDetailsEntity set hasFinished=:hasFinished Where nationalIdentity=:id")
    fun updateCustomerhasFinished(hasFinished: Boolean, id: String)

    @Query(
        "update CustomerDetailsEntity set nationalIdentity=:newNationalID,firstName=:firstName,lastName=:lastName," +
                "alias=:alias,email=:email,subBranchId=:subBranchID,dob=:dob,genderId=:genderId,genderName=:genderName," +
                "spouseName=:spouseName,spousePhone=:spousePhone Where nationalIdentity=:oldNationalID"
    )
    fun updateCustomerNationalID(
        oldNationalID: String,
        newNationalID: String,
        firstName: String,
        lastName: String,
        alias: String,
        email: String,
        subBranchID: String,
        dob: String,
        genderId: String,
        genderName: String,
        spouseName: String?,
        spousePhone: String?
    )

}
package com.deefrent.rnd.fieldapp.room.repos

import androidx.lifecycle.LiveData
import com.deefrent.rnd.fieldapp.room.daos.CustomerDetailsDao
import com.deefrent.rnd.fieldapp.room.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomerDetailsRepository(private val customerDetailsDao: CustomerDetailsDao) {
    suspend fun insertCustomerFullDetails(
        customerDetailsEntity: CustomerDetailsEntity, guarantor: List<Guarantor>,
        collateral: List<Collateral>,
        otherBorrowing: List<OtherBorrowing>,
        household: List<HouseholdMemberEntity>, customerDocs: List<CustomerDocsEntity>
    ) {
        customerDetailsDao.insertCustomerDetailsTransaction(
            customerDetailsEntity,
            guarantor,
            collateral,
            otherBorrowing,
            household,
            customerDocs
        )
    }

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
    ) {
        customerDetailsDao.updateCustomerNationalID(
            oldNationalID, newNationalID, firstName,
            lastName,
            alias,
            email,
            subBranchID,
            dob,
            genderId,
            genderName,
            spouseName,
            spousePhone
        )
    }

    fun deleteCustomerFullDetailsById(nationalIdentity: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.deleteCustomerItemsById(nationalIdentity)
        }
    }

    fun deleteCustomer(customerDetailsEntity: CustomerDetailsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.deleteCustomer(customerDetailsEntity)
        }
    }

    fun upsertCustomer(customerDetailsEntity: CustomerDetailsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.insertCustomer(customerDetailsEntity)
        }
    }

    fun deleteCustomerFullD(nationalIdentity: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.deleteCollateralWithID(nationalIdentity)
        }
    }

    fun fetchCustomerFullDetails(nationalIdentity: String): LiveData<CusomerDetailsEntityWithList> {
        return customerDetailsDao.getCustomerDetailsEntityWithListById(nationalIdentity)
    }

    fun getCustomerFullDetails(nationalIdentity: String): CusomerDetailsEntityWithList? {
        return customerDetailsDao.fetchDetailsEntityWithListById(nationalIdentity)
    }

    fun getIncompleteCustomerDetails(isComplete: Boolean): List<CusomerDetailsEntityWithList> {
        return customerDetailsDao.getAllIncompleteCustomerList(isComplete)
    }

    fun getCompleteOfflineCustomerDetails(hasFinished: Boolean): List<CusomerDetailsEntityWithList> {
        return customerDetailsDao.getAllCompleteOfflineCustomerList(hasFinished)
    }

    /**update room*/
    fun getAllCollById(parentId: String): LiveData<List<Collateral>> {
        return customerDetailsDao.getCollByParentID(parentId)
    }

    fun updateCollateral(coll: Collateral, customerDocsEntity: CustomerDocsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateCollateralAndDoc(coll, customerDocsEntity)
        }
    }


    suspend fun deleteCollateral(collateralID: Int, docGeneratedUID: String) {
        customerDetailsDao.deleteCollateral(collateralID, docGeneratedUID)
    }

    fun getAllGuarById(parentId: String): LiveData<List<Guarantor>> {
        return customerDetailsDao.getGuarByParentID(parentId)
    }

    fun updateGuarn(guarantor: Guarantor, customerDocs: List<CustomerDocsEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateGuarantorAndDoc(guarantor, customerDocs)
        }
    }

    fun inSertCollateral(coll: Collateral) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.insertSingleCollateral(coll)
        }
    }

    fun insertGuarantor(guarantor: Guarantor) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.insertSingleGuarantor(guarantor)
        }
    }

    fun insertCustomerDocsEntity(customerDocsEntity: CustomerDocsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.insertSingleDocs(customerDocsEntity)
        }
    }

    suspend fun deleteGuarantor(id: Int, parentID: String, docGeneratedUID: String) {
        customerDetailsDao.deleteGuarantor(id, parentID, docGeneratedUID)
    }

    fun getAllBorrowingById(parentId: String): LiveData<List<OtherBorrowing>> {
        return customerDetailsDao.getBorrowingByParentID(parentId)
    }

    fun updateBorrowing(borrow: OtherBorrowing) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateBorrowing(borrow)
        }
    }

    suspend fun deleteBorrow(id: Int) {
        customerDetailsDao.deleteBorrowWithID(id)
    }

    fun getCustomerDocs(parentId: String): List<CustomerDocsEntity> {
        return customerDetailsDao.getCustomerDocs(parentId)
    }

    fun updateHMember(householdMemberEntity: HouseholdMemberEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateHMemberWithID(householdMemberEntity)
        }
    }

    suspend fun deleteHMember(id: Int) {
        customerDetailsDao.deleteHMemberWithID(id)
    }

    fun updateCustomerLastStep(isComplete: Boolean, id: String, lastStep: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateCustomerLastStep(isComplete, id, lastStep)
        }

    }

    fun updateCustomerIsProcessed(isProcessed: Boolean, id: String, customerId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateCustomerIsProcessed(isProcessed, id, customerId)
        }

    }

    fun updateCustomerHasFinished(hasFinished: Boolean, id: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsDao.updateCustomerhasFinished(hasFinished, id)
        }

    }
}
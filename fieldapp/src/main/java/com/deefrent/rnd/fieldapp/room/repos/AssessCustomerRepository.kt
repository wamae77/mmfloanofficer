package com.deefrent.rnd.fieldapp.room.repos

import android.util.Log
import androidx.lifecycle.LiveData
import com.deefrent.rnd.fieldapp.room.daos.AssessCustomerDao
import com.deefrent.rnd.fieldapp.room.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AssessCustomerRepository(private val assessCustomerDao: AssessCustomerDao) {
    fun insertAssessedCustomer(assessCustomerEntity:AssessCustomerEntity,
                               custDoc: List<AssessCustomerDocsEntity>,
                               coll: List<AssessCollateral>,
                               gua: List<AssessGuarantor>,
                               borrow: List<AssessBorrowing>,
                               household: List<AssessHouseholdMemberEntity>
                               ){
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.insertAssessedCustomerTransaction(assessCustomerEntity,custDoc,coll, gua, borrow, household)
            Log.e("TAG", "insertAssessmentDataZZ999: ${assessCustomerEntity.assessmentPercentage}", )

        }
    }
    fun deleteAssessedCustomer(assessCustomerEntity:AssessCustomerEntity){
        GlobalScope.launch (Dispatchers.IO){
            assessCustomerDao.deleteAssessedCustomer(assessCustomerEntity)
        }
    }
    /**call this directly to fragment*/
    fun getIncompleteAssessed(isComplete:Boolean):List<AssessCustomerEntityWithList>{
        return assessCustomerDao.getIncompleteAssessedCustomer(isComplete)
    }
    fun getOfflineAssessed(hasFinished:Boolean):List<AssessCustomerEntityWithList>{
        return assessCustomerDao.getOfflineAssessedCustomer(hasFinished)
    }
     fun fetchCustomerFullDetails(nationalIdentity:String):LiveData<AssessCustomerEntityWithList>{
        return assessCustomerDao.getCustomerDetailsEntityWithListById(nationalIdentity)
    }

    fun updateCollateral(coll: AssessCollateral,customerDocsEntity: AssessCustomerDocsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.updateCollateralAndDoc(coll,customerDocsEntity)
        }
    }

    suspend fun deleteCollateral(collateralID: Int, docGeneratedUID: String) {
        assessCustomerDao.deleteAssessedCollateral(collateralID, docGeneratedUID)
    }

    fun updateGuarn(guarantor: AssessGuarantor,customerDocs: List<AssessCustomerDocsEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.updateGuarantorAndDoc(guarantor,customerDocs)
        }
    }

    suspend fun deleteGuarantor(id: Int,parentID: String,docGeneratedUID: String) {
        assessCustomerDao.deleteGuarantor(id,parentID,docGeneratedUID)
    }
    fun updateBorrowing(borrow: AssessBorrowing) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.updateBorrowing(borrow)
        }
    }

    suspend fun deleteBorrow(id: Int) {
        assessCustomerDao.deleteBorrowWithID(id)
    }

    fun getCustomerDocs(parentId: String): List<AssessCustomerDocsEntity> {
        return assessCustomerDao.getCustomerDocs(parentId)
    }

    fun updateHMember(householdMemberEntity: AssessHouseholdMemberEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.updateHMemberWithID(householdMemberEntity)
        }
    }

    suspend fun deleteHMember(id: Int) {
        assessCustomerDao.deleteHMemberWithID(id)
    }
    fun updateCustomerLastStep(assessmentRemarks:String,isComplete:Boolean,id: String,lastStep:String) {
        GlobalScope.launch (Dispatchers.IO){
            assessCustomerDao.updateCustomerLastStep(assessmentRemarks,isComplete, id, lastStep)
        }

    }
    fun updateCustomerHasFinished(hasFinished:Boolean,id: String) {
        GlobalScope.launch (Dispatchers.IO){
            assessCustomerDao.updateCustomerhasFinished(hasFinished, id)
        }

    }
    fun updateCustomerIsProcessed(isProcessed:Boolean,id: String) {
        GlobalScope.launch (Dispatchers.IO){
            assessCustomerDao.updateCustomerIsProcessed(isProcessed, id)
        }

    }
    fun inSertCollateral(coll: AssessCollateral) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.insertSingleCollateral(coll)
        }
    }
    fun insertGuarantor(guarantor: AssessGuarantor) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.insertSingleGuarantor(guarantor)
        }
    }
    fun insertCustomerDocsEntity(customerDocsEntity: AssessCustomerDocsEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerDao.insertSingleDocs(customerDocsEntity)
        }
    }



}
package com.deefrent.rnd.fieldapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.AssessCustomerDTO
import com.deefrent.rnd.fieldapp.dtos.CheckCreditScoreDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.responses.GetCreditScoreResponse
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.repos.AssessCustomerRepository
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class AssessmentDashboardViewModel : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusDocCode = MutableLiveData<Int?>()
    val statusDocCode: MutableLiveData<Int?>
        get() = _statusDocCode
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _responseAssessStatus = MutableLiveData<GeneralResponseStatus>()
    val responseAssessStatus: LiveData<GeneralResponseStatus>
        get() = _responseAssessStatus
    private var _responseCreditScoreStatus = MutableLiveData<GeneralResponseStatus>()
    val responseCreditScoreStatus: LiveData<GeneralResponseStatus>
        get() = _responseCreditScoreStatus
    private var _creditScoreData = MutableLiveData<GetCreditScoreResponse>()
    val creditScoreData: LiveData<GetCreditScoreResponse>
        get() = _creditScoreData
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseXStatus = MutableLiveData<GeneralResponseStatus>()
    val responseXStatus: LiveData<GeneralResponseStatus>
        get() = _responseXStatus
    private var _incompleteData = MutableLiveData<AssessmentResponse>()
    val incompleteData: LiveData<AssessmentResponse>
        get() = _incompleteData
    val incompleteItems=MutableLiveData<CustomerAssessmentData>()
     var _customerHouseholdMember = MutableLiveData<List<CustomerHouseholdMember>>()
    val customerHouseholdMember: LiveData<List<CustomerHouseholdMember>>
        get() = _customerHouseholdMember
    var _customerCollateral= MutableLiveData<List<CustomerCollateralInfo>>()
    var customerGuarantor = MutableLiveData<List<CustomerGuarantorInfo>>()
    var customerBorrowings = MutableLiveData<List<CustomerOtherBorrowing>>()
    private val assessCustomerRepository:AssessCustomerRepository
    var assessCustomerEntity = MutableLiveData<AssessCustomerEntity>()
    var isToLocal = MutableLiveData<Boolean>()
    var assessHouseholdMemberEntity = MutableLiveData<List<AssessHouseholdMemberEntity>>()
    var assessGuarantor = MutableLiveData<List<AssessGuarantor>>()
    var assessCollateral = MutableLiveData<List<AssessCollateral>>()
    var assessBorrowing = MutableLiveData<List<AssessBorrowing>>()
    var _customerAssessList = MutableLiveData<List<AssessCustomerEntityWithList>>()

    var creditScore = MutableLiveData<XDSResponse>()
    var customerCompeteWithList = MutableLiveData<List<AssessCustomerEntityWithList>>()
    var customerCompeteOfflineWithList = MutableLiveData<List<AssessCustomerEntityWithList>>()
    fun insertSingleCollateral(coll: AssessCollateral) {
        assessCustomerRepository.inSertCollateral(coll)
    }
    fun insertGuarantor(guarantor: AssessGuarantor) {
        assessCustomerRepository.insertGuarantor(guarantor)
    }
    fun insertDocument(customerDocsEntity: AssessCustomerDocsEntity) {
        assessCustomerRepository.insertCustomerDocsEntity(customerDocsEntity)
    }


    fun removeAssessesDataAtPos(position:Int){
        /** ?.apply runs when the value is not null*/
        _customerAssessList.value?.apply {
            val items=this.toMutableList()
            items.removeAt(position)
            _customerAssessList.value=items
        }
    }
    val parentId=MutableLiveData<String>()
    /**income*/
    val lastname=MutableLiveData<String>()
    fun updateCustomerLastStep(assessmentRemarks:String,isComplete:Boolean,id: String,lastStep:String) {
        assessCustomerRepository.updateCustomerLastStep(assessmentRemarks,isComplete, id, lastStep)
    }
    fun updateCustomerIsProcessed(isProcessed:Boolean,id: String) {
        assessCustomerRepository.updateCustomerIsProcessed(isProcessed, id)
    }
    fun updateCustomerHasFinished(hasFinished:Boolean,id: String) {
        assessCustomerRepository.updateCustomerHasFinished(hasFinished, id)
    }
    fun updateCollateral(coll: AssessCollateral,customerDocsEntity: AssessCustomerDocsEntity) {
        assessCustomerRepository.updateCollateral(coll,customerDocsEntity)
    }

    fun deleteCollateral(collateralID: Int, docGeneratedUID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerRepository.deleteCollateral(collateralID, docGeneratedUID)
        }
    }
    fun updateGuarantor(guarantor: AssessGuarantor,customerDocs: List<AssessCustomerDocsEntity>) {
        assessCustomerRepository.updateGuarn(guarantor,customerDocs)
    }

    fun deleteGuarantor(id: Int,parentID: String,docGeneratedUID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerRepository.deleteGuarantor(id,parentID,docGeneratedUID)
        }
    }
    fun updateBorrow(otherBorrowing: AssessBorrowing) {
        assessCustomerRepository.updateBorrowing(otherBorrowing)
    }

    fun deleteBorrowID(id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerRepository.deleteBorrow(id)
        }
    }

    fun getCustomerDocs(parentID: String): List<AssessCustomerDocsEntity> {
        return assessCustomerRepository.getCustomerDocs(parentID)
    }
    fun updateHMember(householdMemberEntity: AssessHouseholdMemberEntity) {
        assessCustomerRepository.updateHMember(householdMemberEntity)
    }

    fun deleteHMemberID(id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            assessCustomerRepository.deleteHMember(id)
        }
    }


    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun deleteAssessedCustomer(assessCustomerEntity:AssessCustomerEntity){
        assessCustomerRepository.deleteAssessedCustomer(assessCustomerEntity)
    }
    fun insertAssessmentData(assessCustomerEntity:AssessCustomerEntity,
                             custDoc: List<AssessCustomerDocsEntity>,
                             coll: List<AssessCollateral>,
                              gua: List<AssessGuarantor>,
                              borrow: List<AssessBorrowing>,
                              household: List<AssessHouseholdMemberEntity>
                             ){
        Log.e("TAG", "insertAssessmentDataZZZ: ${assessCustomerEntity.assessmentPercentage}", )
        assessCustomerRepository.insertAssessedCustomer(assessCustomerEntity,custDoc,coll, gua, borrow, household)
    }

     fun fetchCustomerDetails(nationalIdentity:String):LiveData<AssessCustomerEntityWithList>
            =  assessCustomerRepository.fetchCustomerFullDetails(nationalIdentity)
    init {
        val assessCustomerDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).assessCustomerDao()
        assessCustomerRepository= AssessCustomerRepository(assessCustomerDao)
        _status.value = null
        _statusCode.value = null
        _statusDocCode.value = null
        //getCustomerIncompleteData()
    }
    fun stopObserving() {
        _status.value = null
        _statusCode.value = null
        _statusDocCode.value = null
    }

     fun getCustomerIncompleteData() {
        uiScope.launch {
            _responseStatus.value= GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.getIncompleteAssessmentAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                      _incompleteData.value = accResponse
                    _responseStatus.value= GeneralResponseStatus.DONE
                    _status.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _responseStatus.value= GeneralResponseStatus.ERROR
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _responseStatus.value= GeneralResponseStatus.ERROR
                e.printStackTrace()
                _status.value =e.hashCode()
            }
        }

    }
    fun assessCustomer(assessCustomerDTO: AssessCustomerDTO) {
        uiScope.launch {
            _responseAssessStatus.value=GeneralResponseStatus.LOADING
            val registerRequest = FieldAgentApi.retrofitService.assessCustomerAsync(assessCustomerDTO)
            try {
                val registerResponse = registerRequest.await()
                when (registerResponse.status) {
                    1 -> {
                        _responseAssessStatus.value=GeneralResponseStatus.DONE
                        _statusCode.value = registerResponse.status
                    }
                    0 -> {
                        _responseAssessStatus.value=GeneralResponseStatus.DONE
                        _statusMessage.value = registerResponse.message
                        _statusCode.value = 0
                    }
                }
            } catch (e: Throwable) {
                _responseAssessStatus.value=GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _statusCode.value = 0
                }else{
                    _statusCode.value = e.hashCode()
                }
            }
        }

    }
    fun checkCreditScore(checkCreditScoreDTO: CheckCreditScoreDTO) {
        uiScope.launch {
            _responseCreditScoreStatus.value=GeneralResponseStatus.LOADING
            val registerRequest = FieldAgentApi.retrofitService.checkCreditScoreAsync(checkCreditScoreDTO)
            try {
                val registerResponse = registerRequest.await()
                when (registerResponse.status) {
                    1 -> {
                        _responseCreditScoreStatus.value=GeneralResponseStatus.DONE
                        _creditScoreData.value=registerResponse
                        _statusCode.value = registerResponse.status
                    }
                    0 -> {
                        _responseCreditScoreStatus.value=GeneralResponseStatus.DONE
                        _statusMessage.value = registerResponse.message
                        _statusCode.value = 0
                    }
                }
            } catch (e: Throwable) {
                _responseCreditScoreStatus.value=GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _statusCode.value = 0
                }else{
                    _statusCode.value = e.hashCode()
                }
            }
        }

    }
    suspend fun uploadCustomerDocs(customerId: RequestBody,
                                   docTypeCode: RequestBody,
                                   channelGeneratedCode: RequestBody,
                                   file: MultipartBody.Part, isLastItem:Boolean):Boolean {
        uiScope.launch(Dispatchers.Main) {
            _responseXStatus.value = GeneralResponseStatus.LOADING
        }

            val registerRequest =
                FieldAgentApi.retrofitService.uploadDocumentAsync(customerId,docTypeCode, channelGeneratedCode, file)
            try {
                val registerResponse = registerRequest.await()
                when (registerResponse.status) {
                    1 -> {
                        if (isLastItem){
                            uiScope.launch(Dispatchers.Main) {
                                _responseXStatus.value = GeneralResponseStatus.DONE
                                _statusDocCode.value = registerResponse.status
                            }

                        }
                        return true


                    }
                    0 -> {
                        uiScope.launch(Dispatchers.Main) {
                            _statusMessage.value = registerResponse.message
                            _statusDocCode.value = 0
                            _responseXStatus.value = GeneralResponseStatus.ERROR
                        }

                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                uiScope.launch(Dispatchers.Main) {
                    _statusDocCode.value = e.hashCode()
                    _responseXStatus.value = GeneralResponseStatus.ERROR
                }


            }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
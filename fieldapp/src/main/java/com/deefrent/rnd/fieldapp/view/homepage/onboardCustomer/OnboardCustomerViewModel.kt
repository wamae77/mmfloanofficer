package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.HomeDashboardItems
import com.deefrent.rnd.fieldapp.dtos.CustomerLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.OnboardCustomerDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.entities.OtherBorrowing
import com.deefrent.rnd.fieldapp.room.repos.CustomerDetailsRepository
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

class OnboardCustomerViewModel @Inject constructor(private val app: Application) :
    AndroidViewModel(app) {
    private var _customerDetails = MutableLiveData<CusomerDetailsEntityWithList?>()
    val customerDetails: LiveData<CusomerDetailsEntityWithList?>
        get() = _customerDetails

    fun clearCustomerDetails() {
        _customerDetails.postValue(null)
    }

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
    private var _accountLookUpData = MutableLiveData<CustomerLookUpData>()
    val accountLookUpData: LiveData<CustomerLookUpData>
        get() = _accountLookUpData
    var frontIdPhoto: File? = null
    var backIdPhoto: File? = null
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseOnStatus = MutableLiveData<GeneralResponseStatus>()
    val responseOnStatus: LiveData<GeneralResponseStatus>
        get() = _responseOnStatus
    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private var _formId = MutableLiveData<Int?>()
    var registered by Delegates.notNull<Boolean>()
    fun removeRegDataAtPos(position: Int) {
        /** ?.apply runs when the value is not null*/
        _customerList.value?.apply {
            val items = this.toMutableList()
            items.removeAt(position)
            _customerList.value = items
        }
    }


    /**customer details*/
    // val isFromIncompleteScreen = MutableLiveData<Boolean>()
    val cIdNumber = MutableLiveData<String>()
    var isFromLookup = false
    val customerPhoneNumber = MutableLiveData<String>()
    //   val isTrackStepFromSummary = MutableLiveData<Boolean>()

    // val customerDocsEntity = MutableLiveData<ArrayList<CustomerDocsEntity>>()
    var customerEntityData = MutableLiveData<CustomerDetailsEntity>()
    var getCustomerEntityData = MutableLiveData<CustomerDetailsEntity>()
    var collateralData = MutableLiveData<List<Collateral>>()
    var borrowingData = MutableLiveData<List<OtherBorrowing>>()
    var householdMemberEntity = MutableLiveData<List<HouseholdMemberEntity>>()

    var _customerList = MutableLiveData<List<CusomerDetailsEntityWithList>>()
    val customerList: LiveData<List<CusomerDetailsEntityWithList>>
        get() = _customerList
    var customerCompeteList = MutableLiveData<List<CusomerDetailsEntityWithList>>()
    var customerCompeteOfflineList = MutableLiveData<List<CusomerDetailsEntityWithList>>()
    var customerId = ""


    fun updateCollateral(coll: Collateral, customerDocsEntity: CustomerDocsEntity) {
        customerDetailsRepository.updateCollateral(coll, customerDocsEntity)
    }

    fun insertSingleCollateral(coll: Collateral) {
        customerDetailsRepository.inSertCollateral(coll)
    }

    fun deleteCollateral(collateralID: Int, docGeneratedUID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsRepository.deleteCollateral(collateralID, docGeneratedUID)
        }
    }

    fun insertGuarantor(guarantor: Guarantor) {
        customerDetailsRepository.insertGuarantor(guarantor)
    }

    fun insertDocument(customerDocsEntity: CustomerDocsEntity) {
        customerDetailsRepository.insertCustomerDocsEntity(customerDocsEntity)
    }

    fun updateGuarantor(guarantor: Guarantor, customerDocs: List<CustomerDocsEntity>) {
        customerDetailsRepository.updateGuarn(guarantor, customerDocs)
    }

    fun deleteGuarantor(id: Int, parentID: String, docGeneratedUID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsRepository.deleteGuarantor(id, parentID, docGeneratedUID)
        }
    }

    fun updateBorrow(otherBorrowing: OtherBorrowing) {
        customerDetailsRepository.updateBorrowing(otherBorrowing)
    }

    fun deleteBorrowID(id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsRepository.deleteBorrow(id)
        }
    }

    fun getCustomerDocs(parentID: String): List<CustomerDocsEntity> {
        return customerDetailsRepository.getCustomerDocs(parentID)
    }

    fun updateHMember(householdMemberEntity: HouseholdMemberEntity) {
        customerDetailsRepository.updateHMember(householdMemberEntity)
    }

    fun deleteHMemberID(id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            customerDetailsRepository.deleteHMember(id)
        }
    }

    fun updateCustomerLastStep(isComplete: Boolean, id: String, lastStep: String) {
        customerDetailsRepository.updateCustomerLastStep(isComplete, id, lastStep)
    }

    fun updateCustomerHasFinished(hasFinished: Boolean, id: String) {
        customerDetailsRepository.updateCustomerHasFinished(hasFinished, id)
    }

    fun updateCustomerIsProcessed(isProcessed: Boolean, id: String, customerId: String) {
        customerDetailsRepository.updateCustomerIsProcessed(isProcessed, id, customerId)
    }


    private val viewModelJob = Job()
    private val customerDetailsRepository: CustomerDetailsRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    fun deleteCustomerDetails(nationalIdentity: String) {
        customerDetailsRepository.deleteCustomerFullDetailsById(nationalIdentity)
    }

    fun deleteCustomerD(customerDetailsEntity: CustomerDetailsEntity) {
        customerDetailsRepository.deleteCustomer(customerDetailsEntity)
        Log.d("TAG", "deleteCustomerD: ${Gson().toJson(customerDetailsEntity)}")
    }

    fun upsertCustomerD(customerDetailsEntity: CustomerDetailsEntity) {
        customerDetailsRepository.upsertCustomer(customerDetailsEntity)
    }

    fun fetchCustomerDetails(nationalIdentity: String): LiveData<CusomerDetailsEntityWithList> =
        customerDetailsRepository.fetchCustomerFullDetails(nationalIdentity)

    fun getCustomerDetails(nationalIdentity: String) {
        clearCustomerDetails()
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(
                "TAG",
                "getCustomerDetDispatchers${
                    customerDetailsRepository.getCustomerFullDetails(nationalIdentity)
                } ",
            )
            _customerDetails.postValue(
                customerDetailsRepository.getCustomerFullDetails(
                    nationalIdentity
                )
            )
        }
    }

    fun insertCustomerFullDetails(
        customerDetailsEntity: CustomerDetailsEntity, guarantor: List<Guarantor>,
        collateral: List<Collateral>,
        otherBorrowing: List<OtherBorrowing>,
        household: List<HouseholdMemberEntity>,
        customerDocs: List<CustomerDocsEntity>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            customerDetailsRepository.insertCustomerFullDetails(
                customerDetailsEntity,
                guarantor,
                collateral,
                otherBorrowing,
                household, customerDocs
            )
        }

    }

    fun updateCustomerNationalID(
        oldNationalID: String, newNationalID: String, firstName: String,
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
        viewModelScope.launch(Dispatchers.IO) {
            customerDetailsRepository.updateCustomerNationalID(
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

    }

    init {
        val customerDetailsDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).customerDetailsDao()
        customerDetailsRepository = CustomerDetailsRepository(customerDetailsDao)
        _status.value = null
        _statusCode.value = null
        _statusDocCode.value = null

    }

    fun stopObserving() {
        _status.value = null
        _statusCode.value = null
        _statusDocCode.value = null
    }

    fun accountLookup(customerLookUpDTO: CustomerLookUpDTO) {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.customerAccLookUpAsync(customerLookUpDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    Log.d("TAG", "accountLookup: test")
                    _accountLookUpData.value = accResponse.data
                    registered = accResponse.data.registered
                    /*AppPreferences.apply {
                        maxCollateral = accResponse.data.maxCollaterals
                        minCollateral = accResponse.data.minCollaterals
                        maxGuarantor = accResponse.data.maxGuarantors
                        minGuarantor = accResponse.data.minGuarantors
                    }*/
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _status.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _status.value = 0
                }

            } catch (e: Throwable) {
                Log.d("TAG", "accountLookup: ${e.localizedMessage}")
                Log.d("TAG", "accountLookup: rr${e.message}")
                _responseStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if ("${e.message}".contains("failed to connect")) {
                    _status.value = 0
                } else {
                    Log.e("TAG", "loginUser: ${e.message}")
                    _status.value = e.hashCode()
                }
            }
        }

    }

    fun onBoardCustomerFirst(onboardCustomerDTO: OnboardCustomerDTO) {
        uiScope.launch {
            _responseOnStatus.value = GeneralResponseStatus.LOADING
            val registerRequest =
                FieldAgentApi.retrofitService.onboardCustomerAsync(onboardCustomerDTO)
            try {
                val registerResponse = registerRequest.await()
                when (registerResponse.status) {
                    1 -> {
                        Constants.customerId = registerResponse.data.customerId
                        customerId = registerResponse.data.customerId.toString()
                        Log.d("TAG", "onBoardCustomerFirst: ${Constants.customerId}")
                        _responseOnStatus.value = GeneralResponseStatus.DONE
                        _statusCode.value = registerResponse.status
                    }
                    0 -> {
                        _responseOnStatus.value = GeneralResponseStatus.ERROR
                        _statusMessage.value = registerResponse.message
                        _statusCode.value = 0
                    }
                }
            } catch (e: Throwable) {
                _responseOnStatus.value = GeneralResponseStatus.ERROR
                e.printStackTrace()
                _statusCode.value = e.hashCode()
            }
        }

    }

    suspend fun uploadCustomerDocs(
        customerId: RequestBody,
        docTypeCode: RequestBody,
        channelGeneratedCode: RequestBody,
        file: MultipartBody.Part, isLastItem: Boolean
    ): Boolean {
        Log.d("TAG", "uploadCustomerDocs: $customerId")
        uiScope.launch(Dispatchers.Main) {
            _responseGStatus.value = GeneralResponseStatus.LOADING
        }

        val registerRequest =
            FieldAgentApi.retrofitService.uploadDocumentAsync(
                customerId,
                docTypeCode,
                channelGeneratedCode,
                file
            )
        try {
            val registerResponse = registerRequest.await()
            when (registerResponse.status) {
                1 -> {
                    if (isLastItem) {
                        uiScope.launch(Dispatchers.Main) {
                            _responseGStatus.value = GeneralResponseStatus.DONE
                            _statusDocCode.value = registerResponse.status
                        }

                    }
                    return true
                }
                0 -> {
                    uiScope.launch(Dispatchers.Main) {
                        _statusMessage.value = registerResponse.message
                        _responseGStatus.value = GeneralResponseStatus.ERROR
                        _statusDocCode.value = 0
                    }

                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            uiScope.launch(Dispatchers.Main) {
                _statusDocCode.value = e.hashCode()
                _responseGStatus.value = GeneralResponseStatus.ERROR
            }

        }

        return false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    fun prepareDashboardItems(): ArrayList<HomeDashboardItems> {
        val dashBoardItemsList = ArrayList<HomeDashboardItems>();

        dashBoardItemsList.add(
            HomeDashboardItems(
                1,
                R.drawable.d_onboard_c,
                app.resources.getString(R.string.onboard_account)
            )
        )
        dashBoardItemsList.add(
            HomeDashboardItems(
                2,
                R.drawable.d_asses,
                app.resources.getString(R.string.customer_assessment)
            )
        )
        dashBoardItemsList.add(
            HomeDashboardItems(
                3,
                R.drawable.d_loans,
                app.resources.getString(R.string.customer_loans)
            )
        )
        dashBoardItemsList.add(
            HomeDashboardItems(
                4,
                R.drawable.d_incomp,
                app.resources.getString(R.string.incomplete_onboarding_process)
            )
        )
        dashBoardItemsList.add(
            HomeDashboardItems(
                5,
                R.drawable.target,
                app.resources.getString(R.string.sales_target)
            )
        )
        /*dashBoardItemsList.add(
            HomeDashboardItems(
                R.drawable.ic_bill,
                app.resources.getString(R.string.bill_payment)
            )
        )*/
        dashBoardItemsList.add(
            HomeDashboardItems(
                6,
                R.drawable.d_diary,
                app.resources.getString(R.string.diary)
            )
        )
        dashBoardItemsList.add(
            HomeDashboardItems(
                7,
                R.drawable.d_miliage,
                app.resources.getString(R.string.mileage)
            )
        )
//        dashBoardItemsList.add(
//            HomeDashboardItems(
//                8,
//                R.drawable.funeral_cash_plan,
//                app.resources.getString(R.string.funeral_cash_plan)
//            )
//        )
        return dashBoardItemsList
    }
}

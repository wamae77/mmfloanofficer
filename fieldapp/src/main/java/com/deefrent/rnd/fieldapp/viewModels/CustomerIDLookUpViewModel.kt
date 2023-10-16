package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.GuarantorModel
import com.deefrent.rnd.fieldapp.dtos.CustomerIDLookUpDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CustomerIDLookUpViewModel @Inject constructor() : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _iDLookUpData = MutableLiveData<CustomerIDData?>()
    val iDLookUpData: LiveData<CustomerIDData?>
        get() = _iDLookUpData
    var houseHoldMembers = MutableLiveData<List<HouseholdMember>>()
    var frontIdPhoto: File? = null
    var backIdPhoto: File? = null
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private var _formId = MutableLiveData<Int?>()
    /**guarantors details*/
     var _guarantorList = MutableLiveData<List<GuarantorModel>>()
    val guarantorList: LiveData<List<GuarantorModel>>
        get() = _guarantorList
    private var _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    private val viewModelJob = Job()
    val repo:DropdownItemRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    fun getAllGender():LiveData<List<Gender>>{
        return repo.retrieveAllGender
    }
    init {
        val genderDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo= DropdownItemRepository(genderDao)
        _status.value = null
        _statusCode.value = null
    }
    fun stopObserving() {
        _status.value = null
        _statusCode.value = null
    }
    fun idLookup(idLookUpDTO: CustomerIDLookUpDTO) {
        uiScope.launch {
          //  houseHoldMembers.value= arrayListOf()
            _responseStatus.value=GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.customerIDLookupAsync(idLookUpDTO)
            try {
                val accResponse = request.await()
                    if (accResponse.status == 1) {
                        _responseStatus.value=GeneralResponseStatus.DONE
                        _iDLookUpData.value=accResponse.data
                        Constants.lookupId=accResponse.data.idNumber
                        /*if (accResponse.data.householdMembers.isNotEmpty()){
                            _isEmpty.value=false
                            houseHoldMembers.value = accResponse.data.householdMembers
                        }else{
                            houseHoldMembers.value= arrayListOf()
                            _isEmpty.value=true
                        }*/
                        _status.value = accResponse.status
                    } else {
                        _statusMessage.value = accResponse.message
                        _responseStatus.value=GeneralResponseStatus.DONE
                        _status.value = 0
                    }

            } catch (e: Throwable) {
                _responseStatus.value=GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _status.value =0
                }
                _status.value =e.hashCode()
            }
        }

    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
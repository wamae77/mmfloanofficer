package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.TellerAccountStatmentData
import com.deefrent.rnd.fieldapp.repositories.LookupRepo
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private var lookupRepo:LookupRepo= LookupRepo()
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username
    private var _pin = MutableLiveData<String>()
    val pin: LiveData<String>
        get() = _pin
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _tellerStatement = MutableLiveData<List<TellerAccountStatmentData>>()
    val tellerStatement: LiveData<List<TellerAccountStatmentData>>
        get() = _tellerStatement
    private var _resendStatus = MutableLiveData<Int?>()
    val resendStatus: LiveData<Int?>
        get() = _resendStatus
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _status.value = null
        _statusCode.value = null
    }
    fun stopObserving() {
        _status.value = null
        _statusCode.value = null
    }
    fun changePin(changePinDTO: ChangePinDTO ) {
        uiScope.launch {
            val request= FieldAgentApi.retrofitService.changePinAsync(changePinDTO)
            try {
                val response=request.await()
                if (response!!.status==1){

                    _status.value = 1
                }else if (response.status==0){
                    _statusMessage.value=response.message
                    _status.value=0
                }

            }catch (e: Throwable) {
                e.printStackTrace()
                _status.value = e.hashCode()
            }
        }

    }
    fun getTellerStatement() {
        uiScope.launch {
            _responseStatus.value= GeneralResponseStatus.LOADING
            val request= FieldAgentApi.retrofitService.getTellerAccountBalances()
            try {
                val response=request.await()
                if (response.status==1){
                    _responseStatus.value= GeneralResponseStatus.DONE
                    _tellerStatement.value=response.data
                    _statusCode.value = 1
                }else if (response.status==0){
                    _responseStatus.value= GeneralResponseStatus.ERROR
                    _statusMessage.value=response.message
                    _statusCode.value=0
                }

            }catch (e: Throwable) {
                e.printStackTrace()
                _responseStatus.value= GeneralResponseStatus.ERROR
                _statusCode.value = e.hashCode()
            }
        }

    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
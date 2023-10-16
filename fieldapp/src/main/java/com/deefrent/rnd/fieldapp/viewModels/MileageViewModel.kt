package com.deefrent.rnd.fieldapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.dtos.AddMileageDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateMileageDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.responses.GetMileageResponse
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MileageViewModel:ViewModel() {
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
    private var _responseSta = MutableLiveData<GeneralResponseStatus>()
    val responseSta: LiveData<GeneralResponseStatus>
        get() = _responseSta
    private var _diaryData = MutableLiveData<GetMileageResponse>()
    val diaryData: LiveData<GetMileageResponse>
        get() = _diaryData
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
    fun getMileage() {
        uiScope.launch {
            _responseStatus.value= GeneralResponseStatus.LOADING
            val request= FieldAgentApi.retrofitService.getMileage()
            try {
                val response=request.await()
                if (response.status==1){
                    _responseStatus.value= GeneralResponseStatus.DONE
                    _diaryData.value=response
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
    fun addMileage(addMileageDTO: AddMileageDTO) {
        uiScope.launch {
            _responseSta.value= GeneralResponseStatus.LOADING
            val request= FieldAgentApi.retrofitService.addMileage(addMileageDTO)
            try {
                val response=request.await()
                if (response.status==1){
                    _responseSta.value= GeneralResponseStatus.DONE
                    getMileage()
                    _status.value = 1
                }else if (response.status==0){
                    _responseSta.value= GeneralResponseStatus.ERROR
                    _statusMessage.value=response.message
                    _status.value=0
                }

            }catch (e: Throwable) {
                if ("${e.message}".contains("failed to connect")) {
                    _statusMessage.value="Check your internet connection and try again"
                    _status.value = 0
                } else {
                    Log.e("TAG", "loginUser: ${e.message}")
                    _status.value = e.hashCode()
                }
                _responseSta.value= GeneralResponseStatus.ERROR
            }
        }

    }
    fun updateMileage(updateMileageDTO: UpdateMileageDTO) {
        uiScope.launch {
            _responseSta.value= GeneralResponseStatus.LOADING
            val request= FieldAgentApi.retrofitService.updateMileage(updateMileageDTO)
            try {
                val response=request.await()
                if (response.status==1){
                    _responseSta.value= GeneralResponseStatus.DONE
                    getMileage()
                    _status.value = 1
                }else if (response.status==0){
                    _responseSta.value= GeneralResponseStatus.ERROR
                    _statusMessage.value=response.message
                    _status.value=0
                }

            }catch (e: Throwable) {
                if ("${e.message}".contains("failed to connect")) {
                    _statusMessage.value="Check your internet connection and try again"
                    _status.value = 0
                } else {
                    Log.e("TAG", "loginUser: ${e.message}")
                    _status.value = e.hashCode()
                }
                _responseSta.value= GeneralResponseStatus.ERROR
            }
        }

    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
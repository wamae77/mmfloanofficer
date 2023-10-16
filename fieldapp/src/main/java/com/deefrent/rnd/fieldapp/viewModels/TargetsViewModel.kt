package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.models.targets.Target
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TargetsViewModel:ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _targetsData = MutableLiveData<List<Target>>()
    val targetsData:LiveData<List<Target>>
        get() = _targetsData

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    init {
        _status.value = null
        _statusCode.value = null
        getTargetsData()
    }
    fun stopObserving() {
        _status.value = null
        _statusCode.value = null
    }

    fun getTargetsData() {
        uiScope.launch {
            _responseStatus.value= GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.getTargetsAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _targetsData.value = accResponse.data
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
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
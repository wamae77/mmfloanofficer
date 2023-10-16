package com.deefrent.rnd.fieldapp.view.auth.userlogin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.dtos.VerifyUserDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.TellerAccountData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PinViewModel () : ViewModel() {
    private var _statusVCode = MutableLiveData<Int?>()
    val statusVCode: MutableLiveData<Int?>
        get() = _statusVCode
    private val viewModelJob = Job()
    private var _authSuccess = MutableLiveData<Boolean?>()
    val authSuccess: LiveData<Boolean?>
        get() = _authSuccess
    private var _tellerBal = MutableLiveData<TellerAccountData>()
    val tellerBal: LiveData<TellerAccountData>
        get() = _tellerBal
    fun unsetAuthSuccess(){
        _authSuccess.value=null
    }
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getWalletAccountBal()
        _statusVCode.value=null
        _authSuccess.value=null

    }
    fun verifyUser(verifyUserDTO: VerifyUserDTO){
        uiScope.launch {
            val vUserProperties= FieldAgentApi.retrofitService.verifyUser(verifyUserDTO)

            try {
                val vUserResponse=vUserProperties.await()
                    if (vUserResponse.status==1){
                        _authSuccess.value=true
                        _statusVCode.value=1
                    }else{
                        _statusMessage.value=vUserResponse.message
                        _statusVCode.value=0
                    }

            }catch (e:Exception){
                _statusVCode.value=e.hashCode()
            }

        }
    }
    fun stopObserving(){
        _statusVCode.value=null
        _authSuccess.value=null
    }
    fun getWalletAccountBal() {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.getTellerAccountBalAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _tellerBal.postValue(accResponse.data)
                    Log.d("TAG", "getVillages: getVillages")
                }

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
package com.deefrent.rnd.fieldapp.view.auth.forgetPin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.DefaultPinDTO
import com.deefrent.rnd.fieldapp.dtos.SetSecQuizDTO
import com.deefrent.rnd.fieldapp.dtos.VerifySecQuizDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.NetworkHelper
import com.deefrent.rnd.fieldapp.network.models.SetSecurityQuizData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

enum class GeneralResponseStatus { LOADING, DONE, ERROR }
class ForgetPinViewModel : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
    get() = _status
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
    get() = _statusMessage
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
    get() = _responseStatus
    private var _secQuizData = MutableLiveData<List<SetSecurityQuizData>?>()
    val secQuizData: LiveData<List<SetSecurityQuizData>?>
        get() = _secQuizData
    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _status.value=null
        _statusCode.value=null
        loadSecurityQuiz()
    }

    fun stopObserving(){
        _status.value=null
        _statusCode.value=null
    }

    private fun loadSecurityQuiz(){
        uiScope.launch {
            _responseStatus.value=GeneralResponseStatus.LOADING
            val secRequest= FieldAgentApi.retrofitService.loadSecurityQuizAsync()
            try {
                val response=secRequest.await()
                if(response.status==1){
                    /**for the first item in the list*/
                    _secQuizData.value=response.data
                    _responseStatus.value=GeneralResponseStatus.DONE
                }else{
                    _responseStatus.value=GeneralResponseStatus.DONE
                }

            }catch (e: Throwable) {
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                }
                _responseStatus.value=GeneralResponseStatus.DONE
            }
        }

    }
     fun saveSecurityQuiz(setSecQuizDTO: SetSecQuizDTO){
        uiScope.launch {
            val secRequest= FieldAgentApi.retrofitService.saveSecurityQuizAsync(setSecQuizDTO)
            try {
                val response=secRequest.await()
                if(response.status==1){
                    /**for the first item in the list*/
                    _status.value=1
                }else{
                    _statusMessage.value=response.message
                    _status.value=0
                }

            }catch (e: Throwable) {
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                }
                _status.value=e.hashCode()
            }

        }

    }
    fun verifySecurityQuiz(verifySecQuizDTO: VerifySecQuizDTO){
        uiScope.launch {
            val secRequest= FieldAgentApi.retrofitService.verifySecurityQuizAsync(verifySecQuizDTO)
            try {
                val response=secRequest.await()
                if(response.status==1){
                    /**for the first item in the list*/
                    _statusCode.value=1
                }else{
                    _statusMessage.value=response.message
                    _statusCode.value=0
                }

            }catch (e: Throwable) {
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                }
                _statusCode.value=e.hashCode()
            }

        }

    }
    fun verifyDefaultPin(defaultPinDTO: DefaultPinDTO){
        if (NetworkHelper.isNetworkConnected()) {
            uiScope.launch {
                val getOtpResults= FieldAgentApi.retrofitService.defaultPinAsync(defaultPinDTO )
                try {
                    val response=getOtpResults.await()
                    val code = response.code()
                    val otpResult = response.body()
                    if (code == 200 || code == 201) {
                        if (otpResult!!.status==1){
                            _statusMessage.value=otpResult.message
                            _statusCode.value=1
                        }else{
                            _statusMessage.value=otpResult.message
                            _statusCode.value=otpResult.status
                        }

                    }else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val errorData = JSONObject(errorBody)
                            val message = errorData.getString("message")
                            _statusMessage.value = message
                        } else {
                            _statusMessage.value = BaseApp.applicationContext()
                                .getString(R.string.error_occurred)
                        }
                        _statusCode.value = 0
                    }
                }catch (e: Throwable) {
                    e.printStackTrace()
                    if (e is IOException) {
                        _statusMessage.value = BaseApp.applicationContext()
                            .getString(R.string.no_network_connection)
                    }
                    _statusCode.value = e.hashCode()
                }
            }
        } else {
            _statusMessage.value =
                BaseApp.applicationContext().getString(R.string.no_network_connection)
            _statusCode.value = 0

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
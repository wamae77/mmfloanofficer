package com.deefrent.rnd.fieldapp.view.auth.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.utils.Constants.FINGERPRINT_KEY
import com.deefrent.rnd.common.utils.Constants.FINGERPRINT_SECRET
import com.deefrent.rnd.common.utils.Constants.FINGERPRINT_SERVICENAME
import com.deefrent.rnd.common.utils.Constants.FINGERPRINT_URL
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.NetworkHelper
import com.deefrent.rnd.fieldapp.network.models.LoginData
import com.deefrent.rnd.fieldapp.network.models.LookUpData
import com.deefrent.rnd.fieldapp.network.models.TellerBalance
import com.deefrent.rnd.fieldapp.repositories.LookupRepo
import com.deefrent.rnd.fieldapp.utils.camelCase
import com.deefrent.rnd.fieldapp.utils.formatRequestTime
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AccountLookUpViewModel @Inject constructor() : ViewModel() {
    private var lookupRepo: LookupRepo = LookupRepo()
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusLookup = MutableLiveData<Int?>()
    val statusLookup: MutableLiveData<Int?>
        get() = _statusLookup
    private var _verifyStatus = MutableLiveData<Int?>()
    val verifyStatus: MutableLiveData<Int?>
        get() = _verifyStatus
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
    private var _statusLogoutMessage = MutableLiveData<String?>()
    val statusLogoutMessage: LiveData<String?>
        get() = _statusLogoutMessage
    private var _statusLogoutCode = MutableLiveData<Int?>()
    val statusLogoutCode: MutableLiveData<Int?>
        get() = _statusLogoutCode
    private var _genderCode = MutableLiveData<Int?>()
    val genderCode: MutableLiveData<Int?>
        get() = _genderCode
    private var _formId = MutableLiveData<Int?>()
    val formId: MutableLiveData<Int?>
        get() = _formId
    private var _accountLookUpData = MutableLiveData<LookUpData?>()
    val accountLookUpData: LiveData<LookUpData?>
        get() = _accountLookUpData
    private var _tellerBal = MutableLiveData<TellerBalance>()
    val tellerBal: LiveData<TellerBalance>
        get() = _tellerBal
    private var _logData = MutableLiveData<LoginData>()
    val logData: LiveData<LoginData>
        get() = _logData
    private var _changePassword = MutableLiveData<Boolean>()
    val changePassword: LiveData<Boolean>
        get() = _changePassword
    private var _isSecQuizSet = MutableLiveData<Int>()
    val isSecQuizSet: LiveData<Int>
        get() = _isSecQuizSet
    var frontIdPhoto: File? = null
    var backIdPhoto: File? = null
    private var _resendStatus = MutableLiveData<Int?>()
    val resendStatus: LiveData<Int?>
        get() = _resendStatus
    private var _otpMessage = MutableLiveData<String>()
    val otpMessage: LiveData<String>
        get() = _otpMessage

    fun setPin(value: String) {
        _pin.value = value
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _status.value = null
        _statusCode.value = null
        _statusLookup.value = null
        _resendStatus.value = null
        _verifyStatus.value = null
        _statusLogoutCode.value = null
        _statusLogoutMessage.value = null
    }

    fun stopObserving() {
        _status.value = null
        _statusLookup.value = null
        _resendStatus.value = null
        _verifyStatus.value = null
        _statusCode.value = null
        _statusLogoutCode.value = null
        _statusLogoutMessage.value = null
    }

    fun accountLookup(accountLookUpDTO: AccountLookUpDTO) {
        if (NetworkHelper.isNetworkConnected()) {
            uiScope.launch {
                val request = lookupRepo.accountLookup(accountLookUpDTO)
                try {
                    val accResponse = request.await()
                    /**for use in our recyclerview which is listing all the sacco one is registered into"*/
                    if (accResponse.status == 1) {
                        _accountLookUpData.value = accResponse.data
                        if (accResponse.data != null) {
                            _username.value = accResponse.data.username
                        }
                        _statusLookup.value = accResponse.status
                    } else {
                        _statusMessage.value = accResponse.message
                        _statusLookup.value = 0
                    }

                } catch (e: Throwable) {
                    e.printStackTrace()
                    _statusLookup.value = e.hashCode()
                    Log.e("TAG", "accountLookup: ${e.message}")
                }
            }
        } else {
            _statusMessage.value =
                BaseApp.applicationContext().getString(R.string.no_network_connection)
            _statusLookup.value = 0

        }
    }

    fun verifyDeviceOTP(verifyOtpDTO: VerifyOtpDTO) {
        if (NetworkHelper.isNetworkConnected()) {
            uiScope.launch {
                val getOtpResults = lookupRepo.verifyOtp(verifyOtpDTO)
                try {
                    val otpResult = getOtpResults.await()
                    val code = otpResult.code()
                    val response = otpResult.body()
                    if (code == 200 || code == 201) {
                        when (response!!.status) {
                            1 -> {
                                _statusMessage.value = response.message
                                _verifyStatus.value = 1
                            }

                            0 -> {
                                _statusMessage.value = response.message
                                _verifyStatus.value = response.status
                            }
                        }
                    } else {
                        val errorBody = otpResult.errorBody()?.string()
                        if (errorBody != null) {
                            val errorData = JSONObject(errorBody)
                            val message = errorData.getString("message")
                            _statusMessage.value = message
                        } else {
                            _statusMessage.value = BaseApp.applicationContext()
                                .getString(R.string.error_occurred)
                        }
                        _verifyStatus.value = 0
                    }

                } catch (e: Throwable) {
                    e.printStackTrace()
                    if (e is IOException) {
                        _statusMessage.value = BaseApp.applicationContext()
                            .getString(R.string.no_network_connection)
                        _verifyStatus.value = 0
                    } else {
                        _verifyStatus.value = e.hashCode()
                    }
                }
            }
        } else {
            _statusMessage.value =
                BaseApp.applicationContext().getString(R.string.no_network_connection)
            _verifyStatus.value = 0

        }
    }

    fun resendOTP() {
        if (NetworkHelper.isNetworkConnected()) {
            uiScope.launch {
                val resetPassDTO = ResetPassDTO()
                resetPassDTO.username = _username.value!!
                val getResults = lookupRepo.resendOtp(resetPassDTO)
                try {
                    val result = getResults.await()
                    val code = result.code()
                    val response = result.body()
                    if (code == 200 || code == 201) {
                        if (response!!.status == 1) {
                            _otpMessage.value = response.message
                            _resendStatus.value = 1
                        } else {
                            _otpMessage.value = response.message
                            _resendStatus.value = 0
                        }

                    } else {
                        val errorBody = result.errorBody()?.string()
                        if (errorBody != null) {
                            val errorData = JSONObject(errorBody)
                            val message = errorData.getString("message")
                            _otpMessage.value = message
                        } else {
                            _otpMessage.value = BaseApp.applicationContext()
                                .getString(R.string.error_occurred)
                        }
                        _resendStatus.value = 0
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    if (e is IOException) {
                        _otpMessage.value = BaseApp.applicationContext()
                            .getString(R.string.no_network_connection)
                        _resendStatus.value = 0
                    } else {
                        _resendStatus.value = e.hashCode()
                    }
                }
            }
        } else {
            _otpMessage.value =
                BaseApp.applicationContext().getString(R.string.no_network_connection)
            _resendStatus.value = 0

        }
    }

    fun loginUser(loginDTO: LoginDTO) {
        uiScope.launch {
            val getLoginProperties = lookupRepo.loginUser(loginDTO)
            try {
                val loginResults = getLoginProperties.await()
                if (loginResults.status == 1) {
                    //
                    val gson = Gson()
                    val jsonString = gson.toJson(loginResults.data)
                    CommonSharedPreferences(BaseApp.applicationContext()).saveStringData(
                        key = CommonSharedPreferences.CURRENY_USER_DATA,
                        value = jsonString
                    )
                    //
                    FINGERPRINT_URL = loginResults.data.fingerprintAuthLink
                    FINGERPRINT_SERVICENAME = loginResults.data.fingerprintServiceName
                    FINGERPRINT_KEY = loginResults.data.fingerprintConsumerKey
                    FINGERPRINT_SECRET = loginResults.data.fingerprintConsumeSecret
                    //
                    _username.value = loginResults.data.user.username
                    AppPreferences.setPreference(
                        BaseApp.applicationContext(),
                        "token",
                        loginResults.data.token
                    )
                    AppPreferences.setPreference(
                        BaseApp.applicationContext(), "logintime",
                        formatRequestTime(loginResults.data.lastLogin)
                    )
                    AppPreferences.setPreference(
                        BaseApp.applicationContext(),
                        "isFirstTimeLogin",
                        loginResults.data.isFirstLogin.toString().trim()
                    )
                    val loginStatus = loginResults.data.isFirstLogin
                    _isSecQuizSet.value = loginResults.data.securityQuestionsSet
                    _changePassword.value = loginResults.data.changePassword
                    Constants.token = loginResults.data.token
                    _tellerBal.value = loginResults.data.tellerBalance
                    _logData.value = loginResults.data
                    val time =
                        AppPreferences.getPreferences(BaseApp.applicationContext(), "logintime")!!
                    var serverUserName = loginResults.data.user.name.toLowerCase(Locale.US)
                    serverUserName = camelCase(serverUserName)
                    AppPreferences.setPreference(
                        BaseApp.applicationContext(),
                        "username",
                        serverUserName
                    )
                    val splited: List<String> = serverUserName.split("\\s".toRegex())
                    if (splited.count() == 2) {
                        val lastName = splited[1]
                        AppPreferences.setPreference(
                            BaseApp.applicationContext(),
                            "lastName",
                            camelCase(lastName)
                        )
                    }
                    val firstName = splited[0]
                    AppPreferences.setPreference(
                        BaseApp.applicationContext(),
                        "firstName",
                        camelCase(firstName)
                    )
                    AppPreferences.apply {
                        maxCollateral = loginResults.data.maxCollaterals
                        minCollateral = loginResults.data.minCollaterals
                        maxGuarantor = loginResults.data.maxGuarantors
                        minGuarantor = loginResults.data.minGuarantors
                    }
                    Log.d("TAG", "loginUser: ${loginResults.data.maxGuarantors}")
                    _status.value = 1
                } else if (loginResults.status == 0) {
                    _statusMessage.value = loginResults.message
                    _status.value = 0
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("TAG", "loginUser: ${e.hashCode()}")
                Log.e("TAG", "loginUser: ${e.message}")
                if ("${e.message}".contains("failed to connect")) {
                    _statusMessage.value = "Check your internet connection and try again"
                    _status.value = 0
                } else {
                    Log.e("TAG", "loginUser: ${e.message}")
                    _status.value = e.hashCode()
                }
            }
        }
    }

    fun logoutUser() {
        uiScope.launch {
            val getLogoutProperties = lookupRepo.logoutUser()
            try {
                val logoutResults = getLogoutProperties.await()
                if (logoutResults.status == 1) {
                    _statusLogoutCode.value = 1
                    _statusLogoutMessage.value = logoutResults.message
                } else if (logoutResults.status == 0) {
                    _statusLogoutMessage.value = logoutResults.message
                    _statusLogoutCode.value = 0
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("TAG", "loginUser: ${e.hashCode()}")
                Log.e("TAG", "loginUser: ${e.message}")
                if ("${e.message}".contains("failed to connect")) {
                    _statusLogoutMessage.value = "Check your internet connection and try again"
                    _statusLogoutCode.value = 0
                } else {
                    Log.e("TAG", "loginUser: ${e.message}")
                    _statusLogoutCode.value = e.hashCode()
                }
            }
        }
    }

    fun setNewPIn(newPinDTO: NewPinDTO) {
        if (NetworkHelper.isNetworkConnected()) {
            uiScope.launch {
                val getNewPassword = FieldAgentApi.retrofitService.newPinAsync(newPinDTO)
                try {
                    val response = getNewPassword.await()
                    val code = response.code()
                    val passwordResults = response.body()
                    if (code == 200 || code == 201) {
                        if (passwordResults!!.status == 1) {
                            _statusCode.value = passwordResults.status
                        } else if (passwordResults.status == 0) {
                            _statusMessage.value = passwordResults.message
                            _statusCode.value = 0
                        }
                    } else {
                        _statusMessage.value = BaseApp.applicationContext()
                            .getString(R.string.error_occurred)
                        _statusCode.value = 0
                    }

                } catch (e: Throwable) {
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
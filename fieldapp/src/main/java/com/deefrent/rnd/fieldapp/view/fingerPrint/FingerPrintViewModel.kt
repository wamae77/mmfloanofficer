package com.deefrent.rnd.fieldapp.view.fingerPrint

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.data.fingerprint.FingerPrintEnrollmentResponse
import com.deefrent.rnd.common.data.request.UpdateFingerprintRegIdRequets
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.MEDIA_TYPE_FOR_FILES
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CommonResponse
import com.deefrent.rnd.fieldapp.network.repos.FingerPrintRepository
import com.deefrent.rnd.fieldapp.utils.createRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class FingerPrintViewModel @Inject constructor(
    private val repository: FingerPrintRepository,
    private val app: Application
) : AndroidViewModel(app) {

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    fun enrollWithMultipleImages(
        idNumber: String,
        finger_index: String,
        hand_type: String,
        mutableListOfFiles: MutableList<MultipartBody.Part>
    ): Flow<ResourceNetworkFlow<FingerPrintEnrollmentResponse>> {

        val id_number = createRequestBody(idNumber)
        val fingerIndex = createRequestBody(finger_index)
        val handType = createRequestBody(hand_type)

        return repository.enrollWithMultipleImages(
            id_number = id_number,
            finger_index = fingerIndex,
            hand_type = handType,
            images = mutableListOfFiles
        )
    }

    fun enrollCustomerWithMultipleImages(
        idNumber: String,
        finger_index: String,
        hand_type: String,
    ): Flow<ResourceNetworkFlow<FingerPrintEnrollmentResponse>> {
        val id_number = createRequestBody(idNumber)
        val fingerIndex = createRequestBody(finger_index)
        val handType = createRequestBody(hand_type)
        return repository.enrollWithMultipleImages(
            id_number = id_number,
            finger_index = fingerIndex,
            hand_type = handType,
            images = getFingerPrintImages(idNumber).toList()
        )
    }


    fun loginUsersWithMultipleImages(): Flow<ResourceNetworkFlow<CommonResponse>> {
        val fileProfile = File(AUTH_IMAGE_FILE_PATH)
        val requestFileProfile: RequestBody = fileProfile.asRequestBody(MEDIA_TYPE_FOR_FILES)

        val profilePhoto =
            MultipartBody.Part.createFormData(
                "candidate_print",
                fileProfile.name,
                requestFileProfile
            )
        val userUid =
            createRequestBody(commonSharedPreferences.getStringData(CommonSharedPreferences.CU_FINGER_PRINT_ID))
        return repository.loginUsersWithMultipleImages(
            user_uid = userUid,
            candidate_print = profilePhoto,
        )
    }

    fun loginUsersTestWithMultipleImages(userUid: String): Flow<ResourceNetworkFlow<CommonResponse>> {

        val fileProfile = File(AUTH_IMAGE_FILE_PATH)

        val requestFileProfile: RequestBody = fileProfile.asRequestBody(MEDIA_TYPE_FOR_FILES)

        val profilePhoto =
            MultipartBody.Part.createFormData(
                "candidate_print",
                fileProfile.name,
                requestFileProfile
            )


        return repository.loginUsersWithMultipleImages(
            user_uid = createRequestBody(userUid),
            candidate_print = profilePhoto,
        )
    }

    private fun getFingerPrintImages(customer_phonenumber: String): HashSet<MultipartBody.Part> {
        var customerFingerImages: HashSet<MultipartBody.Part> = HashSet()
        viewModelScope.launch {
            repository.getListFingerPrintDataByPhoneNumber(customer_phonenumber)
                .collect {
                    Log.e("PHONENUMBER", "${it.map { it.phoneNumber }}")
                    it.map { dataModel ->
                        /* val assetImage =
                             ImageUtil.convertStringToBitMap(dataModel.fingerImage)
                         val rnds = (100..10000).random()
                         val uri =
                             convertBitmapToFile(app.applicationContext, "${rnds}Asset", assetImage)
                         //val uri: Uri = file.toUri()
                         //val uri = getImageUri(assetImage, "Asset")
                         val uriPathHelper = URIPathHelper()
                         val imageFile = uriPathHelper.getPath(app.applicationContext, uri)!!*/
                        val mediaType = "image/png".toMediaTypeOrNull()
                        val fileAsset = File("${dataModel.fingerImage}")
                        val requestFileAsset = fileAsset.asRequestBody(mediaType)
                        val assetPhotoUrl =
                            MultipartBody.Part.createFormData(
                                "images",
                                fileAsset.name,
                                requestFileAsset
                            )
                        if (customerFingerImages.size != 5) {
                            customerFingerImages.add(assetPhotoUrl)
                        }
                    }
                }
        }
        Log.e("NUMBER OF IMAGES", "${customerFingerImages.size}")
        return customerFingerImages
    }

    fun updateFingerprintRegId(updateFingerprintRegIdRequets: UpdateFingerprintRegIdRequets) =
        repository.updateFingerprintRegId(updateFingerprintRegIdRequets)
    /**
     * ------------------------------------------------------------------------
     *  ROOM
     */

    fun getFingerPrintDataRoom() = repository.getListFingerPrintDataRoom()

    /** Get By Finger print  */
    suspend fun getListFingerPrintDataByPhoneNumber(phoneNumber: String) =
        repository.getListFingerPrintDataByPhoneNumber(phoneNumber = phoneNumber)

    /** SAVE  */
    suspend fun saveFingerPrintDataRoom(data: FingerPrintData) =
        repository.saveListFingerPrintDataRoom(data)

    /** SAVE  */
    suspend fun deleteByPhoneNumber(phoneNumber: String) =
        repository.deleteByPhoneNumber(phoneNumber = phoneNumber)

}


class SharedViewModelToStoreImageData : ViewModel() {
    /**
     *Utilizing  MutableLiveData of AssetDataModel
     */
    private var _assetData = MutableLiveData<MutableList<FingerPrintData>>()
    val assetData get() = _assetData
    private val list = ArrayList<FingerPrintData>()

    fun deleteAssetData() {
        list.clear()
    }

    fun deleteSingleAssetData(assetDataModel: FingerPrintData) {
        list.remove(assetDataModel)
    }

    fun saveAssetData(assetDataModel: FingerPrintData) = viewModelScope.launch {
        list.add(assetDataModel)
        _assetData.value = list
    }
}

/**
 * A shared View Model with example
 */
class SharedViewModelTeToStoreImageData : ViewModel() {
    /**
     *Utilizing  MutableLiveData of AssetDataModel
     */
    var name = MutableLiveData<String>()
    fun setName(assetDataModel: String) = viewModelScope.launch {
        name.value = assetDataModel
    }
}

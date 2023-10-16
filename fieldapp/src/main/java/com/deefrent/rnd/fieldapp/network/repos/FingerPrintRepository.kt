package com.deefrent.rnd.fieldapp.network.repos

import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.data.request.UpdateFingerprintRegIdRequets
import com.deefrent.rnd.common.data.roomdb.MoneyMartAppDatabase
import com.deefrent.rnd.common.repo.BaseRepository
import com.deefrent.rnd.fieldapp.network.FieldApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FingerPrintRepository @Inject constructor(
    private val apiService: FingerPrintApiService,
    private val fieldApiService: FieldApiService,
    private val moneyMartAppDatabase: MoneyMartAppDatabase
) :
    BaseRepository {

    fun enrollUser(
        phone: RequestBody,
        finger_index: RequestBody,
        hand_type: RequestBody,
        image: MultipartBody.Part? = null,
    ) = apiRequestByResourceFlow {
        apiService.enrollUser(
            phone = phone,
            finger_index = finger_index,
            hand_type = hand_type,
            image = image
        )
    }

    fun enrollWithMultipleImages(
        id_number: RequestBody,
        finger_index: RequestBody,
        hand_type: RequestBody,
        images: List<MultipartBody.Part?>?
    ) = apiRequestByResourceFlow {
        apiService.enrollWithMultipleImages(
            id_number = id_number,
            finger_index = finger_index,
            hand_type = hand_type,
            images = images
        )
    }

    fun loginUsersWithMultipleImages(
        user_uid: RequestBody,
        candidate_print: MultipartBody.Part? = null,
    ) = apiRequestByResourceFlow {
        apiService.loginUsersWithMultipleImages(
            user_uid = user_uid,
            candidate_print = candidate_print,
        )
    }

    fun updateFingerprintRegId(
        updateFingerprintRegIdRequets: UpdateFingerprintRegIdRequets
    ) = apiRequestByResourceFlow {
        fieldApiService.updateFingerprintRegId(updateFingerprintRegIdRequets = updateFingerprintRegIdRequets)
    }

    /**
     *  ROOM
     */

    /** SAVE  */
    suspend fun saveListFingerPrintDataRoom(data: FingerPrintData) =
        withContext(Dispatchers.IO) {
            moneyMartAppDatabase.getFingerPrintDataDao().insertData(data)
        }

    /** Get  */
    fun getListFingerPrintDataRoom(): Flow<List<FingerPrintData>> {
        return moneyMartAppDatabase.getFingerPrintDataDao().getAllData()
    }

    /** Get By Finger print  */
    suspend fun getListFingerPrintDataByPhoneNumber(phoneNumber: String): Flow<List<FingerPrintData>> {
        return withContext(Dispatchers.IO) {
            moneyMartAppDatabase.getFingerPrintDataDao()
                .getByPhoneNumber(phoneNumber = phoneNumber)
        }
    }

    /** Delete  */
    suspend fun deleteByPhoneNumber(phoneNumber: String) =
        withContext(Dispatchers.IO) {
            moneyMartAppDatabase.getFingerPrintDataDao()
                .deleteByPhoneNumber(phoneNumber = phoneNumber)
        }


}
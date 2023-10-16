package com.deefrent.rnd.common.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

//
object Constants {
    /**
     * The Base Package ID path for the application module
     *
     * The app module and all other modules should be relative to this path
     * E.G if base = 'com.ekenya.rnd', then app id would be 'com.ekenya.rnd.app', then module id, like support should be 'com.ekenya.rnd.support'
     */
    @JvmField
    var BASE_PACKAGE_NAME = "com.deefrent.rnd"
    var BASE_URL: String = ""
    private external fun getPINNERURL(): String?
    private external fun getPINNERCERT(): String?
    private external fun getFingerPrintURL(): String?

    var FINGERPRINT_URL = ""
    var FINGERPRINT_SERVICENAME = ""
    var FINGERPRINT_KEY = ""
    var FINGERPRINT_SECRET = ""

    // var PINNER_URL ="sha256/QTlFQ0ZFQTQ3QUQyNjEyODM3OUJCQTA5NjdGNzk3NjMzNTQ4OUI5Qw=="
    //var PINNER_CERT = "test-portal.ekenya.co.ke"
    var PINNER_URL = getPINNERURL().toString()
    var PINNER_CERT = getPINNERCERT().toString()

    var token = ""
    var isDelete = false
    var isFromLocal = false
    const val TIMEDIFF: Int = 0
    var DEVICE_ID = ""
    fun deleteCacheImageFromInternalStorage(context: Context, directoryName: String) {
        Log.d("TAG", "deleteImageFromInternalStorage: $directoryName")
        val contextWrapper = ContextWrapper(context)
        val dir = File("${contextWrapper.cacheDir.absolutePath}${File.separator}$directoryName")
        dir.list()?.forEach {
            val deleted = File(dir, it).delete()
            Log.e("TAG", "deleteCacheImageFromInternalStorage:${deleted},$it ")

        }

    }

    var SaveNameFrom = ""
    var SaveIdFrom: Int = 0
    var SaveACNOFROM = ""

    var IS_AUTHENTICATED: Boolean = false
}

/**
 * OTHER CONFIGURATIONS
 */

var MEDIA_TYPE_FOR_FILES = "image/*".toMediaTypeOrNull()
val GENERAL_ACTIVITY_RESULT = 1

/**
 * Profile Picture
 */

var IMAGE_PROFILE_FILE_NAME = ""
var IMAGE_PROFILE_BITMAP: Bitmap? = null
var IMAGE_PROFILE_TEST = ""
var TYPE_OF_HAND: String = "1"
var TYPE_OF_FINGER: String = "1"
var NUMBER_OF_FINGER_BEING_TAKEN: String = "1"
var CUSTOMER_PHONE_NUMBER_AT_FP_TAKING: String = ""

//
const val SUCCESS_DIALOGDETAILCOMMON = "SUCCESS_MESSAGE"
const val SUCCESS_TITLE = "SUCCESS_TITLE"
const val SUCCESS_DESCRIPTION = "SUCCESS_DESCRIPTION"

const val CURRENCY_CODE = "USD"
var TRANSACTION_CHARGES = "0"
var TRANSACTION_EXERCISE_DUTY = ""

var MY_PERMISSIONS = arrayOf(
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE",
    "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
    "android.permission.WRITE_OWNER_DATA",
    "android.permission.READ_OWNER_DATA",
    "android.hardware.usb.accessory",
    "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION",
    "android.permission.HARDWARE_TEST",
    "android.hardware.usb.host"
)
const val REQUEST_CODE = 1

var AUTH_IMAGE_FILE_PATH = ""




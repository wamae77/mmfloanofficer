package com.deefrent.rnd.fieldapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.github.ybq.android.spinkit.SpinKitView
import com.gne.pm.PM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Constants {
    companion object {
        const val frontIDCode = "FRONT-ID-DOC"
        const val profilePicCode = "PROFILE-DOC"
        const val educationDocCode = "EDUCATION-DOC"
        const val businessDocCode = "BUSINESS-DOC"
        const val residenceDocCode = "RESIDENCE-DOC"
        const val paySlipDocCode = "INCOME-PAYSLIP-DOC"
        const val salesReportDocCode = "INCOME-SALES-REPORT-DOC"
        const val rentalIncomeDocCode = "INCOME-RENTAL-DOC"
        const val expenseDocCode = "EXPENSE-DOC"
        const val guarantorDocCode = "GUARANTOR-DOC"
        const val guarantorAFFIDAVITDocCode = "GUARANTOR-AFFIDAVIT-DOC"
        const val guarantorIDDocCode = "GUARANTOR-FRONT-ID-DOC"
        const val collateralDocCode = "COLLATERAL-DOC"
        const val IMAGES_DIR = "FieldApp"
        const val PROFILE_DIR = "FieldApp"
        val pattern = Regex("^https")
        var fromSummary = -1
        var isSummaryBackArrow = false
        var isFromCustomerDetails = false
        var fromSummarySuccess = false
        var lookupId: String = ""
        var customerId: Int = 0
        private var progressDialog: ProgressDialog? = null
        fun callDialog(message: String?, context: Context?) {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(message)
            progressDialog!!.show()
            val handler = Handler()
            val runnable = Runnable {
                progressDialog?.dismiss()
            }
            progressDialog?.setOnDismissListener(DialogInterface.OnDismissListener {
                handler.removeCallbacks(
                    runnable
                )
            })

            handler.postDelayed(runnable, 1000)
        }

        fun callDialog2(message: String?, context: Context?) {
            Handler(Looper.getMainLooper()).post {
                progressDialog = ProgressDialog(context)
                progressDialog!!.setMessage(message)
                progressDialog!!.show()
            }

        }

        fun cancelDialog() {
            progressDialog!!.dismiss()
        }

    }

    val CLOSE_FINGER_PRINT_READER = PM.powerOff()

}

fun <Any> MutableList<Any>.mapInPlace(mutator: (Any) -> (Any)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)
        this[i] = changedValue
    }
}

@SuppressLint("Range")
fun getFileName(uri: Uri, activity: Activity): String {
    val uriString = uri.toString() //The uri with the location of the file
    val returnedFile = File(uriString)
    val absolutePath = returnedFile.absolutePath
    var displayName = ""

    if (uriString.startsWith("content://")) {
        var cursor: Cursor? = null
        try {
            cursor =
                uri.let { activity.contentResolver.query(it, null, null, null, null) }
            if (cursor != null && cursor.moveToFirst()) {
                displayName =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor!!.close()
        }
    } else if (uriString.startsWith("file://")) {
        displayName = returnedFile.name
    }
    return displayName
}

@SuppressLint("Range")
fun getFileName2(uri: Uri, context: Context): String {
    val uriString = uri.toString() //The uri with the location of the file
    val returnedFile = File(uriString)
    val absolutePath = returnedFile.absolutePath
    var displayName = ""

    if (uriString.startsWith("content://")) {
        var cursor: Cursor? = null
        try {
            cursor =
                uri.let { context.contentResolver.query(it, null, null, null, null) }
            if (cursor != null && cursor.moveToFirst()) {
                displayName =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor!!.close()
        }
    } else if (uriString.startsWith("file://")) {
        displayName = returnedFile.name
    }
    return displayName
}

/*fun getFileFromInternalStorage(directoryPath: String, name: String, context: Context): File? {
    val contextWrapper = ContextWrapper(context)
    val directory: File = contextWrapper.getDir(directoryPath, Context.MODE_PRIVATE)

    try {
        return File(directory.absolutePath, name)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return null
}*/

val String.capitalizeWords
    get() = this.lowercase(Locale.getDefault()).split(" ").joinToString(" ") { myString ->
        myString.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun convertPathToFile(imagePath: String): File {
    return File(imagePath)
}

fun generateImageName(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
    val timeStamp: String = dateFormat.format(Date())
    return "field_app_$timeStamp.png"
}

fun formatMonthFromPicker(month: Int): String {
    Log.d("TAG", "formatMonthFromPicker: $month")
    lateinit var formattedMonth: String
    when (month.toString()) {
        "0" -> formattedMonth = "JANUARY"
        "1" -> formattedMonth = "JANUARY"
        "2" -> formattedMonth = "FEBRUARY"
        "3" -> formattedMonth = "MARCH"
        "4" -> formattedMonth = "APRIL"
        "5" -> formattedMonth = "MAY"
        "6" -> formattedMonth = "JUNE"
        "7" -> formattedMonth = "JULY"
        "8" -> formattedMonth = "AUGUST"
        "9" -> formattedMonth = "SEPTEMBER"
        "10" -> formattedMonth = "OCTOBER"
        "11" -> formattedMonth = "NOVEMBER"
        "12" -> formattedMonth = "DECEMBER"
    }
    return formattedMonth
}

fun doubleToStringNoDecimal(d: Double): String? {
    val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
    formatter.applyPattern("#,###")
    return formatter.format(d)
}

fun formatLineChartLabels(date: String): String {
    val formattedDate: Date
    var formattedString = ""
    val format = SimpleDateFormat("yyyy-MM-dd")
    val format2 = SimpleDateFormat("dd-MM")
    try {
        formattedDate = format.parse(date)
        formattedString = format2.format(formattedDate).toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedString
}

fun formatScannedDOB(date: String): String {
    val formattedDate: Date
    var formattedString = ""
    val format = SimpleDateFormat("dd.MM.yyyy")
    val format2 = SimpleDateFormat("yyyy-MM-dd")
    try {
        formattedDate = format.parse(date)
        formattedString = format2.format(formattedDate).toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedString
}

private var pagerPosition = 0
fun autoPlayAdvertisement(viewPager: ViewPager) {
    Handler(Looper.getMainLooper()).postDelayed(Runnable {
        if (pagerPosition == Objects.requireNonNull(viewPager.adapter)!!.count - 1
        ) {
            pagerPosition = 0
            viewPager.currentItem = pagerPosition
        } else {
            viewPager.currentItem = 1.let {
                pagerPosition += it;
                pagerPosition
            }
        }
        autoPlayAdvertisement(viewPager)
    }, 4000)
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun formatDate(date: String): String {
    val originalDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)?.parse(date)
    return SimpleDateFormat("dd/MM/yyyy | h:mm a", Locale.ENGLISH).format(originalDate)
}

fun formatDate2(date: String): String {
    date.let {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)//
        val dateFormat = format.parse(date)
        return SimpleDateFormat("MMM d, yyyy ", Locale.getDefault()).format(dateFormat)
        // this.text = weekdayString.toString()
    }
}

fun formatYear(date: String): String {
    date.let {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)//
        val dateFormat = format.parse(date)
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(dateFormat)
        // this.text = weekdayString.toString()
    }
}

fun formatLivingSince(date: String): String {
    date.let {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)//
        val dateFormat = format.parse(date)
        return SimpleDateFormat("yyyy ", Locale.getDefault()).format(dateFormat)
        // this.text = weekdayString.toString()
    }
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun camelCase(stringToConvert: String): String {
    if (TextUtils.isEmpty(stringToConvert)) {
        return ""
    }
    return Character.toUpperCase(stringToConvert[0]) +
            stringToConvert.substring(1).toLowerCase(Locale.US)
}

fun formatRequestTime(time: String?): String {
    return when {
        time != null -> {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)//
            val dateFormat = format.parse(time)
            val weekdayString =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(dateFormat!!)
            weekdayString.toString()

        }

        else -> {
            ""
        }
    }
}

fun isNetwork(context: Context): Boolean {
    val service = Context.CONNECTIVITY_SERVICE
    val manager = context.getSystemService(service) as ConnectivityManager?
    val network = manager?.activeNetworkInfo
    return (network?.isConnected) ?: false
}

/**
 * Failed transaction request dialog
 * @param context
 * @param responseMessage
 */
fun onNoNetworkDialog(context: Context?) {
    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
    sweetAlertDialog.setContentText(context!!.getString(R.string.no_network_connection))
        .setCustomImage(R.drawable.ic_connection_error)
        .setConfirmClickListener { obj: Dialog -> obj.dismiss() }
        .show()
    sweetAlertDialog.setCancelable(false)
}

fun uniqueFileName(): String {
    val randomNum = (0..1000000).random()
    val date = Date()
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSSSSS", Locale.ENGLISH)
    return "${dateFormat.format(date)}_$randomNum"
}

fun createMultipartRequestBody(file: File, name: String, mediaType: String): MultipartBody.Part {
    val requestFile = file.asRequestBody(mediaType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}

fun createRequestBody(value: Any): RequestBody =
    value.toString().toRequestBody("text/plain".toMediaTypeOrNull())

fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String {
    val cursor = activity.contentResolver.query(contentURI, null, null, null, null)
    val realPath: String? = if (cursor == null) {
        contentURI.path
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        cursor.getString(idx)
    }
    cursor?.close()

    return realPath!!
}


fun bindLoadingBtnClick(progressBar: SpinKitView, status: GeneralResponseStatus?) {
    when (status) {
        GeneralResponseStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }

        GeneralResponseStatus.DONE -> {
            progressBar.visibility = View.GONE
        }

        GeneralResponseStatus.ERROR -> {
            progressBar.visibility = View.GONE
        }

        else -> {}
    }
}

fun getInitials(string: String): String {
    val separated = string.trim().split(" ")
    if (separated.size > 1) {
        return separated.joinToString("") {
            it.first().toString()
        }
    }
    if (separated.first().length > 2) {
        return separated.first().substring(0, 2)
    }
    return string

}

fun getCameraPath(uri: Uri, activity: Activity): String {
    val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getBitmap(
            activity.contentResolver,
            uri
        )
    } else {
        val source =
            ImageDecoder.createSource(activity.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
    val filename = generateImageName()
    val destination =
        File(
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/Camera",
            filename
        )
    val fo: FileOutputStream
    try {
        fo = FileOutputStream(destination)
        fo.write(bytes.toByteArray())
        fo.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return destination.absolutePath
}

fun saveImageToInternalAppStorage(uri: Uri, context: Context, fileName: String): String {
    val bitmapImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                context.contentResolver,
                uri
            )
        )
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
    val contextWrapper = ContextWrapper(context)
    // return a directory in internal storage
    val directory = contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
    // Create a file to save the image
    val file = File(directory, fileName)
    var fos: FileOutputStream
    try {
        GlobalScope.launch(Dispatchers.IO) {
            fos = FileOutputStream(file)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return directory.absolutePath
}

fun generateUniqueDocName(customerID: String, docCode: String): String {
    val generatedDocName = "$customerID-$docCode"
    return "${generatedDocName}.jpg"
}

fun generateUniqueCollateralDocName(generatedUID: String): String {
    return "${generatedUID}.jpg"
}

fun generateUniqueGuarantorDocName(generatedUID: String, docCode: String): String {
    val generatedDocName = "$docCode-$generatedUID"
    return "${generatedDocName}.jpg"
}

fun getImageFromInternalStorage(context: Context, imageFileName: String): Bitmap? {
    val directory = context.filesDir
    val file = File(directory, imageFileName)
    return BitmapFactory.decodeStream(FileInputStream(file))
}

fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
    Log.d("TAG", "deleteImageFromInternalStorage: $imageFileName")
    val contextWrapper = ContextWrapper(context)
    val dir = contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
    val file = File(dir, imageFileName)
    return file.delete()
}

fun deleteCacheImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
    Log.d("TAG", "deleteImageFromInternalStorage: $imageFileName")
    val contextWrapper = ContextWrapper(context)
    /*val dir = File
    val file = File(dir, imageFileName)*/
    Log.e("TAG", "deleteCacheImageFromInternalStorage:${contextWrapper.cacheDir.absolutePath} ")
    return true
}

private fun cachePath(context: Context) =
    "${context.cacheDir.path}${File.separator}compressor${File.separator}"

fun getInitials2(string: String): String {
    val separated = string.trim().split(" ")
    if (separated.size > 2) {
        return "${separated[0].first()} ${separated[1].first()}"
    }
    if (separated.first().length > 2) {
        return separated.first().substring(0, 2)
    }
    return string
}

/*fun save() {
    GlobalScope.launch(Dispatchers.IO) {
        getApplication<Application>().openFileOutput(filename, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, it)
        }
    }
}*/


/*fun createMultipartRequestBody(file: File, mediaType: String, name: String): MultipartBody.Part {
    val requestFile = file.asRequestBody(mediaType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}*/

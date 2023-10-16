package com.deefrent.rnd.common.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.deefrent.rnd.common.R

import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.Period

import java.math.BigInteger
import java.text.NumberFormat
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.util.*


fun Fragment.basicAlert(message: String) {
    AlertDialog.Builder(requireActivity())
        .setTitle(getString(R.string.error_message))
        .setMessage(message)
        .setPositiveButton(getString(R.string.ok)) { dialogInterface, which ->
        }
        .show()
}

/**
 *SnackBar method to be reused
 */
private fun View.showSnackBar(message: String, action: (() -> Unit)? = null) {
    val sB = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        sB.setAction("Retry") {
            it()
        }
    }
    sB.show()
}

fun Fragment.onBackPressedCallback() {
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
}

/**
 * Method fro enable view in class
 */
fun View.isViewEnabled(isButtonEnabled: Boolean) {
    isEnabled = isButtonEnabled
    alpha = if (isButtonEnabled) 1f else 0.5f
}

fun Activity.loadActivity(goToClass: Class<*>) {
    startActivity(Intent(this, goToClass))
}


fun getHasMapData(): HashMap<String, String> {
    var hashMap: java.util.HashMap<String, String> = HashMap()
    hashMap["UserDan"] = "Ajay"
    return hashMap
}

/**
 * Validating phone number
 */

fun isFetchNeeded(savedAt: LocalDateTime): Boolean {
//    if (isConnectionAvailable()) {
//
//    }
    // return ChronoUnit.SECONDS.between(savedAt, LocalDateTime.now()) > 20
    // val thirtyMinutesAgo = LocalDateTime.now().minusSeconds(4)
    // return savedAt.isBefore(thirtyMinutesAgo)
    return true
}

/**
 *SnackBar method to be reused in fragments
 */

fun Fragment.snackBarCustom(msg: String? = "Error", action: (() -> Unit)? = null) {
    val snackbar: Snackbar = Snackbar.make(
        requireActivity()!!.findViewById(android.R.id.content),
        msg.toString(),
        Snackbar.LENGTH_LONG
    )
    val snackbarView = snackbar.view
    val snackTextView =
        snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView

    snackTextView.maxLines = 4
    snackbar.setAction(
        getString(R.string.ok)
    ) { action?.invoke() }
    snackbar.show()
}

fun Activity.snackBarCustom(msg: String, action: (() -> Unit)? = null) {
    val snackbar: Snackbar = Snackbar.make(
        this.findViewById(android.R.id.content),
        msg.toString(),
        Snackbar.LENGTH_LONG
    )
    val snackbarView = snackbar.view
    val snackTextView =
        snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    snackTextView.maxLines = 4
    snackbar.setAction(
        getString(R.string.ok)
    ) { action?.invoke() }
    snackbar.show()
}


// extension function to convert dp to equivalent pixels
fun Int.dpToPixels(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Fragment.makeCameraPermissionsRequest() {
    requestPermissions(
        arrayOf(Manifest.permission.CAMERA),
        200
    )
}

fun Fragment.openCameraForPickingImage(code: Int) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        // startActivityForResult(Intent.createChooser(this, getString(R.string.select_file)), code)
        startActivityForResult(this, code)
    }
}


fun String.getFirstLetter(): String {
    var result = ""
    try {
        //val array = this.split(" ")
        val array = this.split(" ")
        result = if (this.isEmpty()) {
            " "
        } else if (array.size == 1) {
            array[0].substring(0, 1)
        } else if (array.size > 1) {
            array[0].substring(0, 1) + array[1].substring(0, 1)
        } else if (array.size > 0) {
            this[1].toString()
        } else {
            " "
        }
    } catch (e: Exception) {

        result = " "
    }


    return result
}

fun String.getFirstLetter2(): String {
    var result = ""
    try {
        val array = this.split(" ")
        result = if (this.isEmpty()) {
            " "
        } else if (array.size > 0) {
            this[0].toString()
        } else {
            " "
        }
    } catch (e: Exception) {

        result = " "
    }


    return result
}

val maskTextInTextView = object : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {

        override val length: Int
            get() = source.length

        override fun get(index: Int): Char = 'X'

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex)
        }
    }
}

fun Fragment.setTransparentBackground() {
    val bottomSheet = (requireView().parent as View)
    bottomSheet.apply {
        backgroundTintMode = PorterDuff.Mode.CLEAR
        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        setBackgroundColor(Color.TRANSPARENT)
    }
}

fun createSuccessBundle(
    title: String,
    subTitle: String,
    cardTitle: String,
    cardContent: String,
    hashMap: HashMap<String, String>
): Bundle {
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("subtitle", subTitle)
    bundle.putString("cardTitle", cardTitle)
    bundle.putString("cardContent", cardContent)
    bundle.putSerializable("content", hashMap)
    return bundle
}

fun createSuccessBundleDialogDetailCommon(
    title: String,
    subTitle: String,
    dialogDetailCommonArrayList: List<DialogDetailCommon>
): Bundle {
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("subtitle", subTitle)
    bundle.putParcelableArrayList(
        "content",
        dialogDetailCommonArrayList as ArrayList<out Parcelable>
    )
    return bundle
}

fun Fragment.contactDataList(data: Intent?): String {
    val contactData = data!!.data
    var number = ""
    val cursor: Cursor =
        requireActivity().contentResolver
            .query(contactData!!, null, null, null, null)!!
    cursor.moveToFirst()
    val hasPhone =
        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
    val contactId =
        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
    if (hasPhone == "1") {
        val phones: Cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
                    " = " + contactId,
            null,
            null
        )!!
        while (phones.moveToNext()) {
            number =
                phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .replace("[-() ]".toRegex(), "")
        }
        phones.close()


        // Do something with number
    } else {

        number = ""
    }
    cursor.close()

    return number
}


/**
 *Visibility method to be reused to hide views
 */
fun View.visibilityView(isViewVisible: Boolean) {
    visibility = if (isViewVisible) View.VISIBLE else View.GONE
}

fun RecyclerView.disableItemAnimator() {
    itemAnimator!!.endAnimations()
    (itemAnimator as SimpleItemAnimator).changeDuration = 0
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun <T> convertJsonStringToObject(
    jsonString: String,
    clazz: Class<Array<T>>
): Array<T> =
    Gson().fromJson(jsonString, clazz)

fun <T> convertJsonStringToObjectList(
    jsonString: String,
    clazz: Class<T>
): List<T> {
    val gson = Gson()
    val objects = gson.fromJson(jsonString, JsonElement::class.java).asJsonArray
    return objects.map { gson.fromJson(it, clazz) }
}

fun <T> convertJsonObjectToString(listFromString: Class<Array<T>>): String {
    return Gson().toJson(listFromString)
}

val activityResultContractPickContactContract = object : ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(context: Context, input: Uri): Intent {

        return Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
            it.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {

        return if (resultCode != Activity.RESULT_OK || intent == null) null else intent.data
    }
}

fun Activity.hasGooglePlayServices(): Boolean {
    val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
        this.applicationContext
    )
    if (status == ConnectionResult.SUCCESS) {

        return true
    } else {

        return false
    }
}

/**
 * image loader method with glide library
 */
fun ImageView.imageLoader(url: String) {
    Glide.with(this)
        .asBitmap()
        // .placeholder(R.drawable.cargill_icon_logo)
        .load(url)
        .into(this)
}

/**
 * Extension Function to convert string to ascii
 */
fun stringToAsciiNumber(name: String): String {
    var asciiStr = ""
    val iterator = name.iterator()
    for (i in iterator) {
        asciiStr += (i.code.toString() + ",")
    }
    return asciiStr.removeSuffix(",")
}

fun Fragment.isConnectionAvailable(): Boolean {
    var result = false
    val connectivityManager =
        requireActivity().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
}

private var deviceSessionUUID: String? = null


fun cashFormatter(cash: String): String {
    var str: String
    val amountUnformated = cash.toDouble()
    val COUNTRY = "CI" // CI for Côte d'Ivoire
    val LANGUAGE = "fr" // fr for french language
    str = NumberFormat.getCurrencyInstance(Locale(LANGUAGE, COUNTRY)).format(amountUnformated)
    //Timber.d("\n\n WEST AFRICAN CFA FRANC is $str \n\n")
    if (str.contains("CFA")) {
        str = str.replace("CFA", "F")
    }
    return str
}


fun deviceSessionUUID(): String {
    return if (deviceSessionUUID.isNullOrEmpty()) UUID.randomUUID().toString()
    else return deviceSessionUUID as String
}

fun Context.getDeviceId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        .uppercase()
}

fun uuidStringToBigIntPositive2(uuidString: String): BigInteger {
    return BigInteger(uuidString)
}

fun uuidStringFromUuid16(uuid16: String): UUID? {
    // returns a UUID with specified value
    val uuid = UUID.fromString(uuid16)

    val uuid2 = UUID.fromString(
        "5fc03087-d265-11e7-b8c6-83e29cd24f4c"
    )

    Timber.d("Node value: " + uuid2.node()) // returns node value

    //        val big: BigInteger = BigInteger(uuid.toString(), 16)
    //        Timber.d("BigInteger $big")
    return uuid
}


fun getUserIndex(userindex: Int): String {
    var lenght = userindex.toString().length
    return "$lenght$userindex"
}

fun trimAmount(userindex: String): String {
    var lenght = userindex.length
    return "$lenght$userindex"
}

fun getTrimPin(userindex: String): String {
    var lenght = userindex.length
    return "$lenght$userindex"
}

fun trimPhoneNumber(accPhoneNumber: String): String {
    lateinit var phoneNumber: String
    if (accPhoneNumber.startsWith("225")) {
        phoneNumber = accPhoneNumber.drop(4)
    } else {
        phoneNumber = accPhoneNumber.drop(1)
    }
    return phoneNumber
}

fun addLeadingZeroesToNumber(amount: String, digits: Int = 6): String? {
    var output = amount.toString()
    while (output.length < digits) output = "0$output"

    return output
}


fun Fragment.extendStatusBarBackground() {
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    requireActivity().window.statusBarColor = Color.TRANSPARENT
    requireActivity().window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun Activity.extendStatusBarBackground() {
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.window.statusBarColor = Color.TRANSPARENT
    this.window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun Fragment.unExtendStatusBarBackground() {
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    requireActivity().window.statusBarColor = resources.getColor(R.color.transparent)
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

fun Fragment.handleApiErrorForFlow(
    failure: Throwable,
    handleNetworkFailure: (() -> Unit)? = null
) {
    var message = failure.message!!.ifEmpty {
        resources.getString(com.deefrent.rnd.common.R.string.try_again)
    }
    if (message.startsWith("failed to connect")) {
        message = getString(com.deefrent.rnd.common.R.string.check_your_internet_connection)
    }
    //requireView().showSnackBar(message)
    handleNetworkFailure!!.invoke()
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun getDateOfBirth(dob: String): String {
    val splitedDob = dob.split("-")
    val birthDate = LocalDate(splitedDob[0].toInt(), splitedDob[1].toInt(), splitedDob[2].toInt())
    val currentDate = LocalDate.now()
    val age = Period(birthDate, currentDate).years
    Timber.d("The person's age is $age years.")
    return age.toString()
}


/**
 * TextWatcher
 */


fun addTextWatcher(editText: EditText, onTextChanged: (String) -> Unit) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called before the text is changed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This method is called when the text is being changed
            val text = s.toString()
            onTextChanged.invoke(text)
        }

        override fun afterTextChanged(s: Editable?) {
            // This method is called after the text has been changed
        }
    }

    editText.addTextChangedListener(textWatcher)
}


fun getCurrentDateTimeString(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormat.forPattern("dd-MM-yyyy  |  HH:mm a")
    return currentDateTime.toString(formatter)
}





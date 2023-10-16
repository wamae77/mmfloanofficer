package com.deefrent.rnd.fieldapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.facebook.shimmer.ShimmerFrameLayout
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Fragment.showProgress(button: Button, view: View) {
    button.makeGone()
    view.makeVisible()
}

fun Fragment.hideProgress(button: Button, view: View) {
    button.makeVisible()
    view.makeGone()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.onInfoDialog(responseMessage: String?) {
    val sweetd = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
    sweetd
        .setContentText(responseMessage)
        .setConfirmClickListener { obj: Dialog -> obj.dismiss() }
        .show()
    sweetd.setCancelable(false)
}

fun Fragment.onInfoDialogWarn(responseMessage: String?) {
    val sweetd = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
    sweetd
        .setContentText(responseMessage)
        .setConfirmClickListener { obj: Dialog -> obj.dismiss() }
        .show()
    sweetd.setCancelable(false)
}

fun Fragment.onInfoDialogUp(responseMessage: String?) {
    val sweetd = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
    sweetd
        .setContentText(responseMessage)
        .setConfirmClickListener { obj: Dialog ->
            obj.dismiss()
            findNavController().navigateUp()
        }
        .show()
    sweetd.setCancelable(false)
}

fun Fragment.onInfoSuccessDialog(responseMessage: String?) {
    val sweetd = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
    sweetd
        .setContentText(responseMessage)
        .setConfirmClickListener { obj: Dialog ->
            obj.dismiss()
            findNavController().navigateUp()
        }
        .show()
    sweetd.setCancelable(false)
}

fun Fragment.toastySuccess(msg: String) {
    Toasty.success(this.requireContext(), msg, Toasty.LENGTH_SHORT, true).show()
}

fun Fragment.toastyErrors(msg: String) {
    Toasty.error(this.requireContext(), msg, Toast.LENGTH_LONG, true).show()
}

fun Fragment.toastyInfos(msg: String) {
    Toasty
        .info(this.requireContext(), msg, Toast.LENGTH_LONG, true)
        .show()
}

fun TextView.formatDateName(date: String) {
    date?.let {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)//
        val dateFormat = format.parse(date)
        val weekdayString =
            SimpleDateFormat("dd-MM-yyyy | HH:mm a", Locale.getDefault()).format(dateFormat)
        this.text = weekdayString.toString()
    }
}

fun TextView.fragmentFormatDate(date: String) {
    date?.let {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)//
        val dateFormat = format.parse(date)
        val weekdayString =
            SimpleDateFormat("MMM d, yyyy ", Locale.getDefault()).format(dateFormat)
        this.text = weekdayString.toString()
    }
}

fun getGreetings(): String? {
    val date = Date()
    val cal = Calendar.getInstance()
    cal.time = date
    val hour = cal[Calendar.HOUR_OF_DAY]
    var greeting: String?
    greeting = if (hour in 12..16) {
        BaseApp.mresource.getString(R.string.afternoon)
    } else if (hour in 17..20) {
        BaseApp.mresource.getString(R.string.evening)
    } else if (hour in 21..23) {
        BaseApp.mresource.getString(R.string.night)
    } else {
        BaseApp.mresource.getString(R.string.morning)
    }
    return greeting
}

fun Fragment.handleBackButton(destination: Int) {
    val callback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                this@handleBackButton.findNavController().navigate(destination)
            }
        }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

}

fun ImageView.loadImage(
    uri: String?,
    progressView: View? = null,
    placeHolder: Int? = R.drawable.rectangle_placeholder
) {
    progressView?.makeVisible()
    val options = RequestOptions()
    if (placeHolder != null) {
        options.placeholder(placeHolder)
        options.error(placeHolder)
    }

    Glide.with(context)
        .load(uri)
        .apply(options)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressView?.makeGone()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressView?.makeGone()
                return false
            }

        })
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.loadImage2(
    uri: String?,
    progressView: View?,
    placeHolder1: Int? = R.drawable.rectangle_placeholder
) {
    progressView?.makeVisible()
    val options = RequestOptions()
    Glide.with(context)
        .load(uri)
        .apply(options)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressView?.makeGone()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressView?.makeGone()
                return false
            }

        })
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun ImageView.loadShimmerImage(uri: String?, progressView: ShimmerFrameLayout) {
    progressView.startShimmer()
    progressView.makeVisible()
    Glide.with(context)
        .load(uri)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressView.stopShimmer()
                progressView.makeGone()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressView.apply {
                    stopShimmer()
                    makeGone()
                }

                return false
            }

        })
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}


/*
fun TextView.formatNumberComma(number:String){
    val formattedAvailableBalance = StringBuilder()
    val formatter2 = Formatter(formattedAvailableBalance, Locale.forLanguageTag("KSH"))
    val text=formatter2.format(" %(,.2f", number)
    this.text=text
}*/

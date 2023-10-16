package com.deefrent.rnd.fieldapp.utils

import android.app.Dialog
import cn.pedant.SweetAlert.SweetAlertDialog
import com.deefrent.rnd.common.abstractions.BaseActivity

fun BaseActivity.showInfoDialog(responseMessage: String?) {
    val sweetDialog= SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
    sweetDialog
        .setContentText(responseMessage)
        .setConfirmClickListener { obj: Dialog -> obj.dismiss() }
        .show()
    sweetDialog.setCancelable(false)
}
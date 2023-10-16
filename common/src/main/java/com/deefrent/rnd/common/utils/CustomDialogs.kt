package com.deefrent.rnd.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.deefrent.rnd.common.R
import com.deefrent.rnd.common.databinding.ItemDialogImageButtonBinding
import com.deefrent.rnd.common.databinding.ItemDialogOneButtonBinding
import com.deefrent.rnd.common.databinding.ItemDialogTwoButtonBinding

fun Fragment.showTwoButtonDialog(
    title: String,
    description: String,
    btnConfirmTitle: String = getString(R.string.confirm_label),
    btnCancelTitle: String = getString(R.string.cancel_label),
    listenerCancel: (() -> Unit),
    listenerConfirm: (() -> Unit)
) {
    val dialogBinding = ItemDialogTwoButtonBinding.inflate(layoutInflater)

    val alertDialog =
        AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
            .create()

    alertDialog.apply {
        setView(dialogBinding.root)
        setCancelable(false)
    }.show()

    dialogBinding.description.text = description
    dialogBinding.tvTitle.text = title
    if (title.toString().isEmpty()) {
        dialogBinding.tvTitle.visibilityView(false)
    }
    dialogBinding.buttonConfirm.text = btnConfirmTitle
    dialogBinding.buttonConfirm.setOnClickListener {
        listenerConfirm.invoke()
        alertDialog.dismiss()
    }
    dialogBinding.buttonCancel.text = btnCancelTitle
    dialogBinding.buttonCancel.setOnClickListener {
        listenerCancel.invoke()
        alertDialog.dismiss()
    }
}

fun Fragment.showOneButtonDialog(
    title: String,
    description: String,
    image: Int,//= R.drawable.ic_success_check,
    listener: (() -> Unit)? = null
) {
    val dialogBinding = ItemDialogOneButtonBinding.inflate(layoutInflater)

    val alertDialog =
        AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
            .create()

    alertDialog.apply {
        setView(dialogBinding.root)
        setCancelable(false)
    }.show()

    dialogBinding.imageView3.setImageResource(image)
    dialogBinding.description.text = description
    dialogBinding.tvTitle.text = title
    if (title.toString().isEmpty()) {
        dialogBinding.tvTitle.visibilityView(false)
    }
    dialogBinding.buttonConfirm.setOnClickListener {
        listener?.invoke()
        alertDialog.dismiss()
    }

}


fun Fragment.showImageDialog(
    bitmap: Bitmap,
    listenerCancel: (() -> Unit)? = null,
    listenerConfirm: (() -> Unit)? = null
) {
    val dialogBinding = ItemDialogImageButtonBinding.inflate(layoutInflater)

    val alertDialog =
        AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
            .create()

    alertDialog.apply {
        setView(dialogBinding.root)
        setCancelable(false)
    }.show()

    dialogBinding.imageView3.setImageBitmap(bitmap)
    dialogBinding.buttonConfirm.setOnClickListener {
        listenerConfirm?.invoke()
        alertDialog.dismiss()
    }

    dialogBinding.buttonCancel.setOnClickListener {
        listenerCancel?.invoke()
        alertDialog.dismiss()
    }


}

fun showOneButtonDialog(
    activity: Activity,
    title: String,
    description: String,
    image: Int = R.drawable.ic_success_check,
    listener: (() -> Unit)? = null
) {
    val dialogBinding = ItemDialogOneButtonBinding.inflate(activity.layoutInflater)

    val alertDialog =
        AlertDialog.Builder(activity, R.style.Style_Dialog_Rounded_Corner)
            .create()

    alertDialog.apply {
        setView(dialogBinding.root)
        setCancelable(false)
    }.show()

    dialogBinding.imageView3.setImageResource(image)
    dialogBinding.description.text = description
    dialogBinding.tvTitle.text = title
    if (title.toString().isEmpty()) {
        dialogBinding.tvTitle.visibilityView(false)
    }
    dialogBinding.buttonConfirm.setOnClickListener {
        listener?.invoke()
        alertDialog.dismiss()
    }

}
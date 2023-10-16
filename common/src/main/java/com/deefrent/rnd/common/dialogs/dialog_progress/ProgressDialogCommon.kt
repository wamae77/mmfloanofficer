package com.deefrent.rnd.common.dialogs.dialog_progress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.deefrent.rnd.common.R
import com.deefrent.rnd.common.dialogs.base.BaseDialogCommon

class ProgressDialogCommon(context : Context) : BaseDialogCommon(){

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null)
    }

    //dialog builder
    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    // progress message
    private val progressText by lazy {
        dialogView.findViewById<TextView>(R.id.textViewMessage)
    }

    // set progress message
    fun setloadingMessage(message: String?) {
        if(message != null){
            progressText.text = message
            return
        }
        progressText.text = "Loading"
    }

}
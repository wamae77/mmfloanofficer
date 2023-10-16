package com.deefrent.rnd.common.utils

import android.app.Activity
import android.view.View


fun setToolbarTitle(
    title: String,
    clToolBar: com.deefrent.rnd.common.databinding.ToolbarCustomLayoutBinding,
    activity: Activity,
    action: (() -> Unit)? = null
) {
    val toolBar = clToolBar
    clToolBar.toolbar.visibility = View.VISIBLE
    toolBar.toolbarTitle.text = title

    toolBar.ivBackButton.setOnClickListener {

        // activity.onBackPressed()
        if (action == null) {
            activity.onBackPressed()
        } else {
            action.invoke()
        }
    }
}






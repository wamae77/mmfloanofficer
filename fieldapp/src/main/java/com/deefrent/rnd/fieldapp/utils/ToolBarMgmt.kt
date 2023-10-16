package com.deefrent.rnd.fieldapp.utils

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import com.deefrent.rnd.common.R
import com.deefrent.rnd.fieldapp.databinding.AppSubToolBarLayoutBinding
import com.google.android.material.appbar.MaterialToolbar


fun setToolbarTitle(
    toolBarBinding: AppSubToolBarLayoutBinding,
    activity: Activity,
    title: String,
    action: (() -> Unit)? = null
) {
    toolBarBinding.tvTitle.text = title
    toolBarBinding.ivBack.setOnClickListener {
        Log.e("BACK", "")
        // activity.onBackPressed()
        if (action == null) {
            activity.onBackPressed()
        } else {
            action.invoke()
        }
    }
}

fun MaterialToolbar.setBackButton(
    title: Int,
    context: Activity,
    color: Int? = Color.WHITE,
    setNavIcon: Int = R.drawable.ic_arrow_left_vector,
    action: (() -> Unit)? = null
) {
    this.setNavigationIcon(setNavIcon)
    this.setTitle(title)
    this.setTitleTextColor(color!!)
    this.setNavigationOnClickListener { view1: View? ->
        if (action == null) {
            context.onBackPressed()
        } else {
            action.invoke()
        }
    }
}



package com.deefrent.rnd.fieldapp.utils.callbacks

import android.view.View
import com.deefrent.rnd.fieldapp.data.HomeDashboardItems

interface DashboardCallBack {
    fun onItemSelected(pos: Int)
}

interface HomeDashboardItemsCallBack {
    fun onItemSelected(view: View, homeDashboardItems: HomeDashboardItems)
}
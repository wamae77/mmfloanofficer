package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.DiaryList

interface DiaryCallBack {
    fun onItemSelected(pos:Int,diary: DiaryList)
}
package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.AssessCollateral

interface AssessColCallBack {
    fun onItemSelected(pos:Int,lists: AssessCollateral)
}
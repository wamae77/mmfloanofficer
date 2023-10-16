package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.Collateral

interface ColCallBack {
    fun onItemSelected(pos:Int,lists:Collateral )
}
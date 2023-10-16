package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.HouseholdMemberEntity

interface HouseholdMemberEntityCallBack {
    fun onItemSelected(pos:Int,lists: HouseholdMemberEntity)
}
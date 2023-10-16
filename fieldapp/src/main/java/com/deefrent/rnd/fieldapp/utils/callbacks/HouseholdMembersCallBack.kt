package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.HouseholdMember

interface HouseholdMembersCallBack {
    fun onItemSelected(pos:Int,items: HouseholdMember)
}
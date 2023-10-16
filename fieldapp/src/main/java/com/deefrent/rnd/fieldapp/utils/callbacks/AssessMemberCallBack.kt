package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.AssessHouseholdMemberEntity

interface AssessMemberCallBack {
    fun onItemSelected(pos:Int,lists: AssessHouseholdMemberEntity)
}
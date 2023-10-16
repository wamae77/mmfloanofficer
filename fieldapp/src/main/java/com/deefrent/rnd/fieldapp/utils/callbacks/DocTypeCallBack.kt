package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.DocumentType

interface DocTypeCallBack {
    fun onItemSelected(pos:Int,listItems: DocumentType)
}
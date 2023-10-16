package com.deefrent.rnd.fieldapp.utils.callbacks

import android.widget.ImageView
import android.widget.TextView
import com.deefrent.rnd.fieldapp.network.models.DocumentData

interface ViewDocumentCallBack {
    fun onItemSelected(pos: Int, listItems: DocumentData, imageView: ImageView)
    fun onEditDocument(pos: Int, listItems: DocumentData, textView: TextView, imageView: ImageView)
}
package com.deefrent.rnd.fieldapp.models.customerDocuments

import androidx.annotation.Keep

@Keep
data class Document(
    val description: String,
    val docTypeId: Int,
    val docTypeName: String,
    val documentId: Int,
    val url: String
)
package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class CustomerDoc(
    val customerID: String,
    var docCode: String,
    var docPath: String,
    var documentId: Int?
)

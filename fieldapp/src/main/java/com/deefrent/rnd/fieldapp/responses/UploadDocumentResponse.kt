package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customerDocuments.Document
import com.deefrent.rnd.fieldapp.models.mileage.Mileage

@Keep
data class UploadDocumentResponse(
    val `data`: Mileage?,
    val message: String,
    val status: Int
)

@Keep
data class Data(
    val documents: List<Document>
)
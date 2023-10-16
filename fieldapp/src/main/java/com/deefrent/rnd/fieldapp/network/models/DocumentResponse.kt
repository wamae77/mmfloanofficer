package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DocumentResponse(
    @SerializedName("data")
    val `data`: List<DocumentData>,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

data class DocumentData(
    @SerializedName("description")
    val description: String, // This is a document
    @SerializedName("docTypeId")
    val docTypeId: Int, // 50
    @SerializedName("docTypeName")
    val docTypeName: String, // Passport Photo
    @SerializedName("documentId")
    val documentId: Int, // 5
    @SerializedName("url")
    val url: String
)

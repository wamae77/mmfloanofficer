package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DiaryResponse(
    @SerializedName("data")
    val `data`: DiaryData,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)
    data class DiaryData(
        @SerializedName("diaryList")
        val diaryList: List<DiaryList>,
        @SerializedName("eventTypes")
        val eventTypes: List<EventType>
    )
    data class DiaryList(
        @SerializedName("description")
        val description: String?,
        @SerializedName("event_date")
        val eventDate: String, // 2022-12-12 00:00:00
        @SerializedName("event_type_id")
        val eventTypeId: Int, // 1
        @SerializedName("event_type_name")
        val eventTypeName: String, // Cash Collection
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("is_active")
        val isActive: Int, // 1
        @SerializedName("venue")
        val venue: String
    )
    data class EventType(
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String // Cash Collection
    ){
        override fun toString(): String {
            return name
        }
    }

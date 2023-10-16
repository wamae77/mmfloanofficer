package com.deefrent.rnd.fieldapp.data


import com.google.gson.annotations.SerializedName

data class OccupationResponse(
    @SerializedName("data")
    val `data`: List<OccupationTypeData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
    data class OccupationTypeData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    ){
        override fun toString(): String {
            return name
        }
    }

package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "individual_account_details")
class CustomerEntity {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("phoneNo")
    @Expose
    var phoneNo: String? = null

    @SerializedName("idNumber")
    @Expose
    var idNumber: String? = null

    @SerializedName("idType")
    @Expose
    var idType: String? = null

    @SerializedName("surname")
    @Expose
    var surname: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("accountOpeningPurpose")
    @Expose
    var accountOpeningPurpose: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("frontIdPath")
    @Expose
    var frontIdPath: String? = null

    @SerializedName("backIdPath")
    @Expose
    var backIdPath: String? = null

    @SerializedName("passportPhotoPath")
    @Expose
    var passportPhotoPath: String? = null

    @SerializedName("userAccountTypeId")
    @Expose
    var userAccountTypeId = 0

    @SerializedName("personalAccountTypeId")
    @Expose
    var personalAccountTypeId = 0

    @SerializedName("KCBBranchId")
    @Expose
    var kCBBranchId = 0

    @SerializedName("sysUserId")
    @Expose
    var sysUserId = 0

    @SerializedName("employmentType")
    @Expose
    var employmentType = 0

    @SerializedName("income")
    @Expose
    var income: String? = null

    @SerializedName("workLocation")
    @Expose
    var workLocation: String? = null

    @SerializedName("lastStep")
    @Expose
    var lastStep: String? = null

    @SerializedName("completionStatus")
    @Expose
    @ColumnInfo(defaultValue = "false")
    var isComplete = false

    @SerializedName("userType")
    @Expose
    var userType: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null
}
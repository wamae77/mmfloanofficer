package com.deefrent.rnd.fieldapp.room.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "individual_account_details")
public class IndividualAccountDetails {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("phoneNo")
    @Expose
    private String phoneNo;
    @SerializedName("idNumber")
    @Expose
    private String idNumber;
    @SerializedName("idType")
    @Expose
    private String idType;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("accountOpeningPurpose")
    @Expose
    private String accountOpeningPurpose;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("frontIdPath")
    @Expose
    private String frontIdPath;
    @SerializedName("backIdPath")
    @Expose
    private String backIdPath;
    @SerializedName("passportPhotoPath")
    @Expose
    private String passportPhotoPath;
    @SerializedName("userAccountTypeId")
    @Expose
    private int userAccountTypeId;
    @SerializedName("personalAccountTypeId")
    @Expose
    private int personalAccountTypeId;
    @SerializedName("KCBBranchId")
    @Expose
    private int KCBBranchId;
    @SerializedName("sysUserId")
    @Expose
    private int sysUserId;
    @SerializedName("employmentType")
    @Expose
    private int employmentType;
    @SerializedName("income")
    @Expose
    private String income;
    @SerializedName("workLocation")
    @Expose
    private String workLocation;
    @SerializedName("lastStep")
    @Expose
    @Nullable
    private String lastStep;
    @SerializedName("completionStatus")
    @Expose
    @ColumnInfo(defaultValue = "false")
    private boolean complete;
    @SerializedName("userType")
    @Expose
    @Nullable
    private String userType;
    @SerializedName("date")
    @Expose
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Nullable
    public String getUserType() {
        return userType;
    }

    public void setUserType(@Nullable String userType) {
        this.userType = userType;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAccountOpeningPurpose() {
        return accountOpeningPurpose;
    }

    public void setAccountOpeningPurpose(String accountOpeningPurpose) {
        this.accountOpeningPurpose = accountOpeningPurpose;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getFrontIdPath() {
        return frontIdPath;
    }

    public void setFrontIdPath(String frontIdPath) {
        this.frontIdPath = frontIdPath;
    }

    public String getBackIdPath() {
        return backIdPath;
    }

    public void setBackIdPath(String backIdPath) {
        this.backIdPath = backIdPath;
    }

    public String getPassportPhotoPath() {
        return passportPhotoPath;
    }

    public void setPassportPhotoPath(String passportPhotoPath) {
        this.passportPhotoPath = passportPhotoPath;
    }

    public int getUserAccountTypeId() {
        return userAccountTypeId;
    }

    public void setUserAccountTypeId(int userAccountTypeId) {
        this.userAccountTypeId = userAccountTypeId;
    }

    public int getPersonalAccountTypeId() {
        return personalAccountTypeId;
    }

    public void setPersonalAccountTypeId(int personalAccountTypeId) {
        this.personalAccountTypeId = personalAccountTypeId;
    }

    public int getKCBBranchId() {
        return KCBBranchId;
    }

    public void setKCBBranchId(int KCBBranchId) {
        this.KCBBranchId = KCBBranchId;
    }

    public int getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(int sysUserId) {
        this.sysUserId = sysUserId;
    }

    public int getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(int employmentType) {
        this.employmentType = employmentType;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    @Nullable
    public String getLastStep() {
        return lastStep;
    }

    public void setLastStep(@Nullable String lastStep) {
        this.lastStep = lastStep;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

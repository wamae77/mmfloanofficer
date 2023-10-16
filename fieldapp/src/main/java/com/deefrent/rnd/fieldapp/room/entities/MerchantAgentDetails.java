package com.deefrent.rnd.fieldapp.room.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "merchant_agent_details")
public class MerchantAgentDetails {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @Nullable
    private int id;
    @SerializedName("merchantIDNumber")
    @Expose
    @Nullable
    private String merchantIDNumber;
    @SerializedName("merchantSurname")
    @Expose
    @Nullable
    private String merchantSurname;
    @SerializedName("merchantFirstName")
    @Expose
    @Nullable
    private String merchantFirstName;
    @SerializedName("merchantLastName")
    @Expose
    @Nullable
    private String merchantLastName;
    @SerializedName("merchantGender")
    @Expose
    @Nullable
    private String merchantGender;
    @SerializedName("userType")
    @Expose
    @Nullable
    private String userType;
    @SerializedName("userAccountTypeId")
    @Expose
    @Nullable
    private int userAccountTypeId;
    @SerializedName("merchAgentAccountTypeId")
    @Expose
    @Nullable
    private int merchAgentAccountTypeId;
    @SerializedName("longitude")
    @Expose
    @Nullable
    private String longitude;
    @SerializedName("latitude")
    @Expose
    @Nullable
    private String latitude;
    @SerializedName("frontIdPath")
    @Expose
    @Nullable
    private String frontIdPath;
    @SerializedName("backIdPath")
    @Expose
    @Nullable
    private String backIdPath;
    @SerializedName("customerPhotoPath")
    @Expose
    @Nullable
    private String customerPhotoPath;
    @SerializedName("businessName")
    @Expose
    @Nullable
    private String businessName;
    @SerializedName("businessMobileNumber")
    @Expose
    @Nullable
    private String businessMobileNumber;
    @SerializedName("businessEmail")
    @Expose
    @Nullable
    private String businessEmail;
    @SerializedName("businessTypeId")
    @Expose
    @Nullable
    private int businessTypeId;
    @SerializedName("businessNature")
    @Expose
    @Nullable
    private String businessNature;
    @SerializedName("liquidationTypeId")
    @Expose
    @Nullable
    private int liquidationTypeId;
    @SerializedName("liquidationRate")
    @Expose
    @Nullable
    private int liquidationRate;
    @SerializedName("bankCode")
    @Expose
    @Nullable
    private String bankCode;
    @SerializedName("branchCode")
    @Expose
    @Nullable
    private String branchCode;
    @SerializedName("accountName")
    @Expose
    @Nullable
    private String accountName;
    @SerializedName("accountNumber")
    @Expose
    @Nullable
    private String accountNumber;
    @SerializedName("countyCode")
    @Expose
    @Nullable
    private String countyCode;
    @SerializedName("townName")
    @Expose
    @Nullable
    private String townName;
    @SerializedName("roomNo")
    @Expose
    @Nullable
    private String roomNo;
    @SerializedName("dob")
    @Expose
    @Nullable
    private String dob;
    @SerializedName("buldingName")
    @Expose
    @Nullable
    private String buldingName;
    @SerializedName("streetName")
    @Expose
    @Nullable
    private String streetName;
    @SerializedName("termsAndConditionDocPath")
    @Expose
    @Nullable
    private String termsAndConditionDocPath;
    @SerializedName("signatureDocPath")
    @Expose
    @Nullable
    private String signatureDocPath;
    @SerializedName("businessPermitDocPath")
    @Expose
    @Nullable
    private String businessPermitDocPath;
    @SerializedName("companyRegistrationDocPath")
    @Expose
    @Nullable
    private String companyRegistrationDocPath;
    @SerializedName("kraPINPath")
    @Expose
    @Nullable
    private String kraPINPath;
    @SerializedName("businessLicensePath")
    @Expose
    @Nullable
    private String businessLicensePath;
    @SerializedName("goodConductPath")
    @Expose
    @Nullable
    private String goodConductPath;
    @SerializedName("fieldApplicationFormPath")
    @Expose
    @Nullable
    private String fieldApplicationFormPath;
    @SerializedName("lastStep")
    @Expose
    @Nullable
    private String lastStep;
    @SerializedName("completionStatus")
    @Expose
    @ColumnInfo(defaultValue = "false")
    private boolean complete;
    @SerializedName("shopPhotoPath")
    @Expose
    @Nullable
    private String shopPhotoPath;
    @SerializedName("idType")
    @Expose
    private String idType;
    @SerializedName("date")
    @Expose
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    @Nullable
    public String getShopPhotoPath() {
        return shopPhotoPath;
    }

    public void setShopPhotoPath(@Nullable String shopPhotoPath) {
        this.shopPhotoPath = shopPhotoPath;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Nullable
    public String getLastStep() {
        return lastStep;
    }

    public void setLastStep(@Nullable String lastStep) {
        this.lastStep = lastStep;
    }

    @Nullable
    public String getFieldApplicationFormPath() {
        return fieldApplicationFormPath;
    }

    public void setFieldApplicationFormPath(@Nullable String fieldApplicationFormPath) {
        this.fieldApplicationFormPath = fieldApplicationFormPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getMerchantIDNumber() {
        return merchantIDNumber;
    }

    public void setMerchantIDNumber(@Nullable String merchantIDNumber) {
        this.merchantIDNumber = merchantIDNumber;
    }

    @Nullable
    public String getMerchantSurname() {
        return merchantSurname;
    }

    public void setMerchantSurname(@Nullable String merchantSurname) {
        this.merchantSurname = merchantSurname;
    }

    @Nullable
    public String getMerchantFirstName() {
        return merchantFirstName;
    }

    public void setMerchantFirstName(@Nullable String merchantFirstName) {
        this.merchantFirstName = merchantFirstName;
    }

    @Nullable
    public String getMerchantLastName() {
        return merchantLastName;
    }

    public void setMerchantLastName(@Nullable String merchantLastName) {
        this.merchantLastName = merchantLastName;
    }

    @Nullable
    public String getMerchantGender() {
        return merchantGender;
    }

    public void setMerchantGender(@Nullable String merchantGender) {
        this.merchantGender = merchantGender;
    }

    @Nullable
    public String getUserType() {
        return userType;
    }

    public void setUserType(@Nullable String userType) {
        this.userType = userType;
    }

    public int getUserAccountTypeId() {
        return userAccountTypeId;
    }

    public void setUserAccountTypeId(int userAccountTypeId) {
        this.userAccountTypeId = userAccountTypeId;
    }

    public int getMerchAgentAccountTypeId() {
        return merchAgentAccountTypeId;
    }

    public void setMerchAgentAccountTypeId(int merchAgentAccountTypeId) {
        this.merchAgentAccountTypeId = merchAgentAccountTypeId;
    }

    @Nullable
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nullable String longitude) {
        this.longitude = longitude;
    }

    @Nullable
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable String latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public String getFrontIdPath() {
        return frontIdPath;
    }

    public void setFrontIdPath(@Nullable String frontIdPath) {
        this.frontIdPath = frontIdPath;
    }

    @Nullable
    public String getBackIdPath() {
        return backIdPath;
    }

    public void setBackIdPath(@Nullable String backIdPath) {
        this.backIdPath = backIdPath;
    }

    @Nullable
    public String getCustomerPhotoPath() {
        return customerPhotoPath;
    }

    public void setCustomerPhotoPath(@Nullable String customerPhotoPath) {
        this.customerPhotoPath = customerPhotoPath;
    }

    @Nullable
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(@Nullable String businessName) {
        this.businessName = businessName;
    }

    @Nullable
    public String getBusinessMobileNumber() {
        return businessMobileNumber;
    }

    public void setBusinessMobileNumber(@Nullable String businessMobileNumber) {
        this.businessMobileNumber = businessMobileNumber;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public int getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(int businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    @Nullable
    public String getBusinessNature() {
        return businessNature;
    }

    public void setBusinessNature(@Nullable String businessNature) {
        this.businessNature = businessNature;
    }

    public int getLiquidationTypeId() {
        return liquidationTypeId;
    }

    public void setLiquidationTypeId(int liquidationTypeId) {
        this.liquidationTypeId = liquidationTypeId;
    }

    public int getLiquidationRate() {
        return liquidationRate;
    }

    public void setLiquidationRate(int liquidationRate) {
        this.liquidationRate = liquidationRate;
    }

    @Nullable
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(@Nullable String bankCode) {
        this.bankCode = bankCode;
    }

    @Nullable
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(@Nullable String branchCode) {
        this.branchCode = branchCode;
    }

    @Nullable
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(@Nullable String accountName) {
        this.accountName = accountName;
    }

    @Nullable
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(@Nullable String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Nullable
    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(@Nullable String countyCode) {
        this.countyCode = countyCode;
    }

    @Nullable
    public String getTownName() {
        return townName;
    }

    public void setTownName(@Nullable String townName) {
        this.townName = townName;
    }

    @Nullable
    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(@Nullable String roomNo) {
        this.roomNo = roomNo;
    }

    @Nullable
    public String getDob() {
        return dob;
    }

    public void setDob(@Nullable String dob) {
        this.dob = dob;
    }

    @Nullable
    public String getBuldingName() {
        return buldingName;
    }

    public void setBuldingName(@Nullable String buldingName) {
        this.buldingName = buldingName;
    }

    @Nullable
    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(@Nullable String streetName) {
        this.streetName = streetName;
    }

    @Nullable
    public String getTermsAndConditionDocPath() {
        return termsAndConditionDocPath;
    }

    public void setTermsAndConditionDocPath(@Nullable String termsAndConditionDocPath) {
        this.termsAndConditionDocPath = termsAndConditionDocPath;
    }

    @Nullable
    public String getSignatureDocPath() {
        return signatureDocPath;
    }

    public void setSignatureDocPath(@Nullable String signatureDocPath) {
        this.signatureDocPath = signatureDocPath;
    }

    @Nullable
    public String getBusinessPermitDocPath() {
        return businessPermitDocPath;
    }

    public void setBusinessPermitDocPath(@Nullable String businessPermitDocPath) {
        this.businessPermitDocPath = businessPermitDocPath;
    }

    @Nullable
    public String getCompanyRegistrationDocPath() {
        return companyRegistrationDocPath;
    }

    public void setCompanyRegistrationDocPath(@Nullable String companyRegistrationDocPath) {
        this.companyRegistrationDocPath = companyRegistrationDocPath;
    }

    @Nullable
    public String getKraPINPath() {
        return kraPINPath;
    }

    public void setKraPINPath(@Nullable String kraPINPath) {
        this.kraPINPath = kraPINPath;
    }

    @Nullable
    public String getBusinessLicensePath() {
        return businessLicensePath;
    }

    public void setBusinessLicensePath(@Nullable String businessLicensePath) {
        this.businessLicensePath = businessLicensePath;
    }

    @Nullable
    public String getGoodConductPath() {
        return goodConductPath;
    }

    public void setGoodConductPath(@Nullable String goodConductPath) {
        this.goodConductPath = goodConductPath;
    }
}

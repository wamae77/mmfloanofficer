package com.deefrent.rnd.fieldapp.bodies.onboardAccount;

public class CreateMerchantBody {
    String businessName, mobileNumber, email, businessNature, bankCode, branchCode, accountName, accountNumber,
            countyCode, townName, streetName, buldingName, roomNo, longitude, latitude,merchantIDNumber,merchantSurname,merchantFirstName,
            merchantLastName,merchantDob,merchantGender;
    int merchAgentAccountTypeId, userAccountTypeId, businessTpeId, liquidationTypeId, liquidationRate;

    public CreateMerchantBody(String businessName, String mobileNumber, String email,
                              String businessNature, String bankCode, String branchCode,
                              String accountName, String accountNumber, String countyCode,
                              String townName, String streetName, String buldingName, String roomNo,
                              int merchAgentAccountTypeId, int userAccountTypeId, int businessTpeId,
                              int liquidationTypeId, int liquidationRate, String longitude,
                              String latitude,String merchantIDNumber,String merchantSurname,String merchantFirstName,
                              String merchantLastName,String merchantDob,String merchantGender) {
        this.businessName = businessName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.businessNature = businessNature;
        this.bankCode = bankCode;
        this.branchCode = branchCode;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.countyCode = countyCode;
        this.townName = townName;
        this.streetName = streetName;
        this.buldingName = buldingName;
        this.roomNo = roomNo;
        this.merchAgentAccountTypeId = merchAgentAccountTypeId;
        this.userAccountTypeId = userAccountTypeId;
        this.businessTpeId = businessTpeId;
        this.liquidationTypeId = liquidationTypeId;
        this.liquidationRate = liquidationRate;
        this.longitude = longitude;
        this.latitude = latitude;
        this.merchantIDNumber=merchantIDNumber;
        this.merchantFirstName=merchantFirstName;
        this.merchantLastName=merchantLastName;
        this.merchantSurname=merchantSurname;
        this.merchantGender=merchantGender;
        this.merchantDob=merchantDob;
    }
}

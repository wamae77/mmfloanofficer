package com.deefrent.rnd.fieldapp.bodies.onboardAccount;

public class CreateCustomerBody {
    String phoneNo, idNumber, surname, firstName, lastName, dob, gender,
            accountOpeningPurpose, longitude, latitude,companyYouWorkFor;
    int userAccountTypeId, personalAccountTypeId, KCBBranchId, sysUserId, employmentType, income;

    public CreateCustomerBody(int userAccountTypeId, String phoneNo, int personalAccountTypeId,
                              int KCBBranchId, String idNumber, String surname, String firstName,
                              String lastName, int sysUserId, String dob, String gender,
                              int income, String companyYouWorkFor, int employmentType,
                              String accountOpeningPurpose, String longitude, String latitude) {
        this.userAccountTypeId = userAccountTypeId;
        this.phoneNo = phoneNo;
        this.personalAccountTypeId = personalAccountTypeId;
        this.KCBBranchId = KCBBranchId;
        this.idNumber = idNumber;
        this.surname = surname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sysUserId = sysUserId;
        this.dob = dob;
        this.gender = gender;
        this.income = income;
        this.companyYouWorkFor = companyYouWorkFor;
        this.employmentType = employmentType;
        this.accountOpeningPurpose = accountOpeningPurpose;
        this.longitude=longitude;
        this.latitude=latitude;
    }
}

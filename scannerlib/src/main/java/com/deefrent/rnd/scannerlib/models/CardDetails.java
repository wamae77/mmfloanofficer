package com.deefrent.rnd.scannerlib.models;

public class CardDetails {
    private String firstName;
    private String middleName;
    private String surname;
    private String dob;
    private String cardNumber;
    private String placeOfBirth;
    private String gender;
    private String county;
    private String subCounty;
    private String district;
    private String division;
    private String location;
    private String subLocation;
    private String nationality;
    private String dateOfIssue;
    private String placeOfIssue;

    /**
     * accessor for nationality string
     * @return string of user's nationality
     */
    public String getNationality() {
        if (nationality==null)
            return "Unknown";
        return nationality;
    }

    /**
     * mutator for nationality
     * @param nationality string rep. user's nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDistrict() {
        if (district==null)
            return "Unknown";
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDivision() {
        if (division==null)
            return "Unknown";
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getLocation() {
        if (location==null)
            return "Unknown";
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubLocation() {
        if (subLocation==null)
            return "Unknown";
        return subLocation;
    }

    public void setSubLocation(String subLocation) {
        this.subLocation = subLocation;
    }

    public String getCardNumber() {
        if (cardNumber==null)
            return "Unknown";
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPlaceOfBirth() {
        if (placeOfBirth==null)
            return "Unknown";
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getFirstName() {
        if (firstName==null)
            return "Unknown";
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        if (middleName==null)
            return "Unknown";
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        if (surname==null)
            return "Unknown";
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        if (dob==null)
            return "Unknown";
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        if (gender==null)
            return "Unknown";
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCounty() {
        if (county==null)
            return "Unknown";
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSubCounty() {
        if (subCounty==null)
            return "Unknown";
        return subCounty;
    }

    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    public String getDateOfIssue() {
        if (dateOfIssue==null)
            return "Unknown";
        return dateOfIssue;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getPlaceOfIssue() {
        if (placeOfIssue==null)
            return "Unknown";
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }
}

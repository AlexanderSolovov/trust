package com.dai.trust.models.party;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;

@Entity
@DiscriminatorValue("true")
public class Person extends Party {

    @Column(name = "name1")
    private String firstName;

    @Column(name = "name2")
    private String lastName;

    @Column(name = "name3")
    private String middleName;
    
    @Column(name = "name4")
    private String alias;

    @Column(name = "citizenship_code")
    private String citizenshipCode;

    @Column(name = "gender_code")
    private String genderCode;
    
    @Column(name = "id_type_code")
    private String idTypeCode;
    
    @Column(name = "id_number")
    private String idNumber;
    
    @Column(name = "marital_status_code")
    private String maritalStatusCode;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dob;
    
    @Column(name = "is_resident")
    private boolean isResident;
    
    @Column(name = "person_photo_id")
    private String personPhotoId;
    
    public Person() {
        super();
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFullName() {
        String fullName = getFirstName();
        if (getMiddleName()!= null && !getMiddleName().equals("")) {
            if (fullName != null && !fullName.equals("")) {
                fullName = fullName + " " + getMiddleName();
            } else {
                fullName = getMiddleName();
            }
        }
        if (getLastName() != null && !getLastName().equals("")) {
            if (fullName != null && !fullName.equals("")) {
                fullName = fullName + " " + getLastName();
            } else {
                fullName = getLastName();
            }
        }
        return fullName;
    }
    
    public String getCitizenshipCode() {
        return citizenshipCode;
    }

    public void setCitizenshipCode(String citizenshipCode) {
        this.citizenshipCode = citizenshipCode;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getIdTypeCode() {
        return idTypeCode;
    }

    public void setIdTypeCode(String idTypeCode) {
        this.idTypeCode = idTypeCode;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMaritalStatusCode() {
        return maritalStatusCode;
    }

    public void setMaritalStatusCode(String maritalStatusCode) {
        this.maritalStatusCode = maritalStatusCode;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public boolean isIsResident() {
        return isResident;
    }

    public void setIsResident(boolean isResident) {
        this.isResident = isResident;
    }

    public String getPersonPhotoId() {
        return personPhotoId;
    }

    public void setPersonPhotoId(String personPhotoId) {
        this.personPhotoId = personPhotoId;
    }
}

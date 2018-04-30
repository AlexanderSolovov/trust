package com.dai.trust.models.party;

import com.dai.trust.models.AbstractIdEntity;
import com.dai.trust.models.JsonDateOnlyDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "party")
public class Party extends AbstractIdEntity {
    
    @Column(name = "is_private", updatable = false)
    private boolean isPrivate;
    
    @Column(name = "name1")
    private String name1;

    @Column(name = "name2")
    private String name2;

    @Column(name = "name3")
    private String name3;
    
    @Column(name = "name4")
    private String name4;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = JsonDateOnlyDeserializer.class)
    private Date dob;
    
    @Column(name = "is_resident")
    private boolean isResident;
    
    @Column(name = "person_photo_id")
    private String personPhotoId;
    
    @Column(name = "mobile_number")
    private String mobileNumber;
    
    @Column(name = "entity_type_code")
    private String entityTypeCode;
    
    @Column
    private String address;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private String parentId;

    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false, updatable = false)
    private String endApplicationId;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyDocument> documents;
    
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;
    
    @Formula("check_party_editable(id) and status_code != 'historic'")
    private boolean editable;
    
    @Transient
    private List<PartyLog> logs;
    
    public Party() {
        super();
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEndApplicationId() {
        return endApplicationId;
    }

    public void setEndApplicationId(String endApplicationId) {
        this.endApplicationId = endApplicationId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<PartyDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PartyDocument> documents) {
        this.documents = documents;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
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

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public List<PartyLog> getLogs() {
        return logs;
    }

    public void setLogs(List<PartyLog> logs) {
        this.logs = logs;
    }
    
    public String getFullName() {
        String fullName = getName1();
        if (getName3()!= null && !getName3().equals("")) {
            if (fullName != null && !fullName.equals("")) {
                fullName = fullName + " " + getName3();
            } else {
                fullName = getName3();
            }
        }
        if (getName2() != null && !getName2().equals("")) {
            if (fullName != null && !fullName.equals("")) {
                fullName = fullName + " " + getName2();
            } else {
                fullName = getName2();
            }
        }
        return fullName;
    }
}

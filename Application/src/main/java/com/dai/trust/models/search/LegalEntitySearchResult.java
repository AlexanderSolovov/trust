package com.dai.trust.models.search;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class LegalEntitySearchResult implements Serializable {
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String address;
    @Column
    private String ccros;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "entity_type")
    private String entityType;
    @Column(name = "reg_number")
    private String regNumber;
    @Column(name = "establishment_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date establishmentDate;
    @Column(name = "status_code")
    private String statusCode;
    
    public LegalEntitySearchResult(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public String getCcros() {
        return ccros;
    }

    public void setCcros(String ccros) {
        this.ccros = ccros;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}

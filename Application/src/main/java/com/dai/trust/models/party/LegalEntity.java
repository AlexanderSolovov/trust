package com.dai.trust.models.party;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;

@Entity
@DiscriminatorValue("false")
public class LegalEntity extends Party {

    @Column(name = "name1")
    private String name;
    
    @Column(name = "id_number")
    private String regNumber;
    
    @Column(name = "entity_type_code")
    private String entityTypeCode;
    
    @Column(name = "dob")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date establishmentDate;
    
    public LegalEntity() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }
}

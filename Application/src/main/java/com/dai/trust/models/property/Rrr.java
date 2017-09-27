package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.DiscriminatorOptions;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case when right_type_code = 'ccro' then 'ownership' else 'restriction' end")
@DiscriminatorOptions(force = true)
@Table(name = "rrr")
public class Rrr extends AbstractIdEntity {

    @Column(name = "property_id", insertable = false, updatable = false)
    private String propertyId;
    
    @Column(name = "parent_id")
    private String parentId;
    
    @Column(name = "right_type_code")
    private String rightTypeCode;
    
    @Column
    private Integer duration;
    
    @Column(name = "folio_number")
    private String folioNumber;
    
    @Column(name = "reg_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "start_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    
    @Column(name = "end_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    
    @Column
    private String description;
        
    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false, updatable = false)
    private String endApplicationId;

    @Column(name = "termination_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminationDate;
    
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;
        
    public Rrr() {
        super();
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRightTypeCode() {
        return rightTypeCode;
    }

    public void setRightTypeCode(String rightTypeCode) {
        this.rightTypeCode = rightTypeCode;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
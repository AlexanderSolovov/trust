package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "rrr")
public class RrrStatusChanger extends AbstractIdEntity {

    @Column(name = "property_id", updatable = false, insertable = false)
    private String propertyId;

    @Column(name = "parent_id", updatable = false, insertable = false)
    private String parentId;

    @Column(name = "right_type_code", updatable = false, insertable = false)
    private String rightTypeCode;

    @Column(name = "folio_number")
    private String folioNumber;

    @Column(name = "reg_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "application_id", updatable = false, insertable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false)
    private String endApplicationId;

    @Column(name = "termination_application_id")
    private String terminationApplicationId;

    @Column(name = "termination_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminationDate;

    @Column(name = "status_code", insertable = false)
    private String statusCode;

    public RrrStatusChanger() {
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

    public String getTerminationApplicationId() {
        return terminationApplicationId;
    }

    public void setTerminationApplicationId(String terminationApplicationId) {
        this.terminationApplicationId = terminationApplicationId;
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

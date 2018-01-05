package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "property")
public class PropertyStatusChanger extends AbstractIdEntity {
    @Column(name = "parcel_id", insertable = false, updatable = false)
    private String parcelId;
    
    @Column(name = "reg_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "termination_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminationDate;

    @Column(name = "application_id", insertable = false, updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false)
    private String endApplicationId;

    @Column(name = "status_code", insertable = false)
    private String statusCode;

    public PropertyStatusChanger() {
        super();
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
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
}

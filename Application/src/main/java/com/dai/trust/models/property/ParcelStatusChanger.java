package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "parcel")
public class ParcelStatusChanger extends AbstractIdEntity {
    @Column(insertable = false, updatable = false)
    private String uka;
    
    @Column(name = "application_id", insertable = false, updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false)
    private String endApplicationId;

    @Column(name = "status_code", insertable = false)
    private String statusCode;
        
    public ParcelStatusChanger() {
        super();
    }

    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
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
package com.dai.trust.models.property;

import com.dai.trust.models.AbstractLog;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ParcelLog extends AbstractLog {
               
    @Column(name = "status_code")
    private String statusCode;
    
    public ParcelLog(){
        super();
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}

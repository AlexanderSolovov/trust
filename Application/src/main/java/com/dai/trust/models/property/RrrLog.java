package com.dai.trust.models.property;

import com.dai.trust.models.AbstractLog;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RrrLog extends AbstractLog {
               
    @Column(name = "status_code")
    private String statusCode;
    
    public RrrLog(){
        super();
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}

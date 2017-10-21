package com.dai.trust.models.search;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PropertyCodeSearchResult implements Serializable {
    @Id
    private String id;
    
    @Column(name = "prop_number")
    private String propNumber;
    
    @Column(name = "status_code")
    private String statusCode;
    
    @Column(name = "status_name")
    private String statusName;
    
    public PropertyCodeSearchResult(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropNumber() {
        return propNumber;
    }

    public void setPropNumber(String propNumber) {
        this.propNumber = propNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}

package com.dai.trust.models.party;

import com.dai.trust.models.AbstractIdEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "party")
public class PartyStatusChanger extends AbstractIdEntity {
    @Column(name = "parent_id", insertable = false)
    private String parentId;

    @Column(name = "status_code", insertable = false)
    private String statusCode;

    @Column(name = "end_application_id", insertable = false)
    private String endApplicationId;
    
    public PartyStatusChanger() {
        super();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getEndApplicationId() {
        return endApplicationId;
    }

    public void setEndApplicationId(String endApplicationId) {
        this.endApplicationId = endApplicationId;
    }
}

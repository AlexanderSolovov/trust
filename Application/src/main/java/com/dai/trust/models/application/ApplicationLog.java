package com.dai.trust.models.application;

import com.dai.trust.models.AbstractLog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ApplicationLog extends AbstractLog {
    
    @Column(name = "status_code")
    private String statusCode;
    
    @Column
    @JsonIgnore
    private String assignee;
        
    @Column(name = "assignee_name")
    private String assigneeName;
            
    public ApplicationLog(){
        
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }
}

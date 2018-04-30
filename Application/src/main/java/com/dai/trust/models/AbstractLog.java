package com.dai.trust.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

@MappedSuperclass
public class AbstractLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @JsonIgnore
    private String id;

    @Column(name = "action_time")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date actionTime;
        
    @Column(name = "action_user")
    @JsonIgnore
    private String actionUser;
    
    @Column(name = "action_user_name")
    private String actionUserName;
                
    public AbstractLog(){
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionUser() {
        return actionUser;
    }

    public void setActionUser(String actionUser) {
        this.actionUser = actionUser;
    }

    public String getActionUserName() {
        return actionUserName;
    }

    public void setActionUserName(String actionUserName) {
        this.actionUserName = actionUserName;
    }
}

package com.dai.trust.models;

import com.dai.trust.common.SharedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;

/**
 * Abstract class for all entities
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Column(name = "rowversion")
    private Integer version;

    @JsonIgnore
    @Column(name = "action_code", insertable = false, updatable = false)
    private String actionCode;

    @JsonIgnore
    @Column(name = "action_user")
    private String actionUser;

    @JsonIgnore
    @Column(name = "action_time", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date actionTime;

    public AbstractEntity() {
        super();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionUser() {
        return actionUser;
    }

    public void setActionUser(String actionUser) {
        this.actionUser = actionUser;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    @PrePersist
    public void preInsert() {
        setActionCode("i");
        setActionUser(SharedData.getUserName());
    }

    @PreUpdate
    public void preSave() {
        setActionCode("u");
        setActionUser(SharedData.getUserName());
    }
}

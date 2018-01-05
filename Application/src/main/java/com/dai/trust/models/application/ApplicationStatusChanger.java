package com.dai.trust.models.application;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "application")
public class ApplicationStatusChanger extends AbstractIdEntity {

    @Column(name = "app_type_code", updatable = false)
    private String appTypeCode;

    @Column(name = "app_number", insertable = false, updatable = false)
    private String appNumber;

    @Column(name = "approve_reject_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date approveRejectDate;

    @Column(name = "withdraw_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date withdrawDate;

    @Column(name = "status_code", insertable = false)
    private String statusCode;

    @Column(name = "reject_reason", insertable = false)
    private String rejectReason;

    @Column(name = "withdraw_reason", insertable = false)
    private String withdrawReason;

    @Column(name = "complete_date", insertable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date completeDate;

    @Column
    private String assignee;
    
    @Column(name = "assigned_on", insertable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date assignedOn;
    
    @OneToMany(mappedBy = "application")
    private List<ApplicationProperty> properties;

    public ApplicationStatusChanger() {
        super();
    }

    public String getAppTypeCode() {
        return appTypeCode;
    }

    public void setAppTypeCode(String appTypeCode) {
        this.appTypeCode = appTypeCode;
    }

    public String getAppNumber() {
        return appNumber;
    }

    public void setAppNumber(String appNumber) {
        this.appNumber = appNumber;
    }

    public Date getApproveRejectDate() {
        return approveRejectDate;
    }

    public void setApproveRejectDate(Date approveRejectDate) {
        this.approveRejectDate = approveRejectDate;
    }

    public Date getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(Date withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getWithdrawReason() {
        return withdrawReason;
    }

    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public List<ApplicationProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<ApplicationProperty> properties) {
        this.properties = properties;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(Date assignedOn) {
        this.assignedOn = assignedOn;
    }
}

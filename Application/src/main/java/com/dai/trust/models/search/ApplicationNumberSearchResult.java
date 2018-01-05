package com.dai.trust.models.search;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class ApplicationNumberSearchResult implements Serializable {
    @Id
    private String id;
    @Column(name = "app_type_code")
    private String appTypeCode;
    @Column(name ="app_number")
    private String appNumber;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "approve_reject_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date approveRejectDate;
    @Column(name = "lodgement_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lodgementDate;
    @Column(name = "assignee")
    private String assignee;
    
    public ApplicationNumberSearchResult(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getApproveRejectDate() {
        return approveRejectDate;
    }

    public void setApproveRejectDate(Date approveRejectDate) {
        this.approveRejectDate = approveRejectDate;
    }

    public Date getLodgementDate() {
        return lodgementDate;
    }

    public void setLodgementDate(Date lodgementDate) {
        this.lodgementDate = lodgementDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}

package com.dai.trust.models.search;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class ApplicationSearchResult implements Serializable {
    @Id
    private String id;
    @Column(name = "app_type_code")
    private String appTypeCode;
    @Column(name = "app_type")
    private String appType;
    @Column(name ="app_number")
    private String appNumber;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "app_status")
    private String appStatus;
    @Column(name = "approve_reject_date")
    private String approveRejectDate;
    @Column(name = "lodgement_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lodgementDate;
    @Column(name = "assignee")
    private String assignee;
    @Column(name = "assignee_name")
    private String assigneeName;
    @Column(name = "applicant_data")
    private String applicantData;
    @Column
    private String ccros;
    
    public ApplicationSearchResult(){
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

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
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

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getApproveRejectDate() {
        return approveRejectDate;
    }

    public void setApproveRejectDate(String approveRejectDate) {
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getApplicantData() {
        return applicantData;
    }

    public void setApplicantData(String applicantData) {
        this.applicantData = applicantData;
    }

    public String getCcros() {
        return ccros;
    }

    public void setCcros(String ccros) {
        this.ccros = ccros;
    }
}

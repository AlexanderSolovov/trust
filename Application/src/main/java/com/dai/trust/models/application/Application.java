package com.dai.trust.models.application;

import com.dai.trust.models.party.*;
import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "application")
public class Application extends AbstractIdEntity {

    @Column(name = "app_type_code")
    private String appTypeCode;
    
    @Column(name = "app_number")
    private String appNumber;

    @Column(name = "lodgement_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lodgementDate;
    
    @Column(name = "approve_reject_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date approveRejectDate;
    
    @Column(name = "withdraw_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date withdrawDate;
    
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;

    @Column(name = "reject_reason", insertable = false, updatable = false)
    private String rejectReason;

    @Column(name = "withdraw_reason", insertable = false, updatable = false)
    private String withdrawReason;

    @Column(updatable = false)
    private String assignee;
    
    @Column(name = "assigned_on", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date assignedOn;
    
    @Column(name = "complete_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date completeDate;
    
    @Column
    private String comment;
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationDocument> documents;
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationPerson> personApplicants;
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationLegalEntity> legalEntityApplicants;
    
    public Application() {
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

    public Date getLodgementDate() {
        return lodgementDate;
    }

    public void setLodgementDate(Date lodgementDate) {
        this.lodgementDate = lodgementDate;
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

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ApplicationDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ApplicationDocument> documents) {
        this.documents = documents;
    }

    public List<ApplicationPerson> getPersonApplicants() {
        return personApplicants;
    }

    public void setPersonApplicants(List<ApplicationPerson> personApplicants) {
        this.personApplicants = personApplicants;
    }

    public List<ApplicationLegalEntity> getLegalEntityApplicants() {
        return legalEntityApplicants;
    }

    public void setLegalEntityApplicants(List<ApplicationLegalEntity> legalEntityApplicants) {
        this.legalEntityApplicants = legalEntityApplicants;
    }

}
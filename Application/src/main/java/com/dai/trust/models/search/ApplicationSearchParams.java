package com.dai.trust.models.search;

import java.io.Serializable;
import java.util.Date;

/**
 * Used to pass application search parameters
 */
public class ApplicationSearchParams implements Serializable {
    private String number;
    private String typeCode;
    private String applicantName;
    private String applicantIdNumber;
    private String statusCode;
    private String ccroNumber;
    private Date lodgemenetDateFrom;
    private Date lodgemenetDateTo;
    
    public ApplicationSearchParams(){
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantIdNumber() {
        return applicantIdNumber;
    }

    public void setApplicantIdNumber(String applicantIdNumber) {
        this.applicantIdNumber = applicantIdNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCcroNumber() {
        return ccroNumber;
    }

    public void setCcroNumber(String ccroNumber) {
        this.ccroNumber = ccroNumber;
    }

    public Date getLodgemenetDateFrom() {
        return lodgemenetDateFrom;
    }

    public void setLodgemenetDateFrom(Date lodgemenetDateFrom) {
        this.lodgemenetDateFrom = lodgemenetDateFrom;
    }

    public Date getLodgemenetDateTo() {
        return lodgemenetDateTo;
    }

    public void setLodgemenetDateTo(Date lodgemenetDateTo) {
        this.lodgemenetDateTo = lodgemenetDateTo;
    }
}

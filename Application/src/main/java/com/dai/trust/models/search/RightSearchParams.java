package com.dai.trust.models.search;

import java.io.Serializable;

/**
 * Used to pass right search parameters
 */
public class RightSearchParams implements Serializable {
    private String propNumber;
    private String fileNumber;
    private String ukaNumber;
    private String rightTypeCode;
    private String rightholderName;
    private String rightholderIdNumber;
    private String statusCode;
    
    public RightSearchParams(){
    }

    public String getPropNumber() {
        return propNumber;
    }

    public void setPropNumber(String propNumber) {
        this.propNumber = propNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getUkaNumber() {
        return ukaNumber;
    }

    public void setUkaNumber(String ukaNumber) {
        this.ukaNumber = ukaNumber;
    }

    public String getRightTypeCode() {
        return rightTypeCode;
    }

    public void setRightTypeCode(String rightTypeCode) {
        this.rightTypeCode = rightTypeCode;
    }

    public String getRightholderName() {
        return rightholderName;
    }

    public void setRightholderName(String rightholderName) {
        this.rightholderName = rightholderName;
    }

    public String getRightholderIdNumber() {
        return rightholderIdNumber;
    }

    public void setRightholderIdNumber(String rightholderIdNumber) {
        this.rightholderIdNumber = rightholderIdNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}

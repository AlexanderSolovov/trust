package com.dai.trust.models.search;

import com.dai.trust.models.property.ParcelLog;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
public class ParcelSearchResult implements Serializable {
    @Id
    private String id;
    
    @Column(name = "land_type_code")
    private String landTypeCode;
    
    @Column(name = "land_type_name")
    private String landTypeName;
    
    @Column
    private String uka;
    
    @Column(name = "survey_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date surveyDate;

    @Column(name = "hamlet_code")
    private String hamletCode;
    
    @Column(name = "parcel_location")
    private String parcelLocation;
    
    @Column
    private String address;
    
    @Column
    private String comment;
    
    @Column(name = "application_id")
    private String applicationId;
    
    @Column(name = "app_number")
    private String appNumber;

    @Column(name = "end_application_id")
    private String endApplicationId;

    @Column(name = "end_app_number")
    private String endAppNumber;
    
    @Column(name = "status_code")
    private String statusCode;
    
    @Column(name = "status_name")
    private String statusName;
    
    @Column
    private String geom;
    
    @Transient
    private List<PropertyCodeSearchResult> propCodes;
    
    @Transient
    private List<ParcelLog> logs;
    
    public ParcelSearchResult(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandTypeCode() {
        return landTypeCode;
    }

    public void setLandTypeCode(String landTypeCode) {
        this.landTypeCode = landTypeCode;
    }

    public String getLandTypeName() {
        return landTypeName;
    }

    public void setLandTypeName(String landTypeName) {
        this.landTypeName = landTypeName;
    }

    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
    }

    public Date getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(Date surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getHamletCode() {
        return hamletCode;
    }

    public void setHamletCode(String hamletCode) {
        this.hamletCode = hamletCode;
    }

    public String getParcelLocation() {
        return parcelLocation;
    }

    public void setParcelLocation(String parcelLocation) {
        this.parcelLocation = parcelLocation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAppNumber() {
        return appNumber;
    }

    public void setAppNumber(String appNumber) {
        this.appNumber = appNumber;
    }

    public String getEndApplicationId() {
        return endApplicationId;
    }

    public void setEndApplicationId(String endApplicationId) {
        this.endApplicationId = endApplicationId;
    }

    public String getEndAppNumber() {
        return endAppNumber;
    }

    public void setEndAppNumber(String endAppNumber) {
        this.endAppNumber = endAppNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<PropertyCodeSearchResult> getPropCodes() {
        return propCodes;
    }

    public void setPropCodes(List<PropertyCodeSearchResult> propCodes) {
        this.propCodes = propCodes;
    }

    public List<ParcelLog> getLogs() {
        return logs;
    }

    public void setLogs(List<ParcelLog> logs) {
        this.logs = logs;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }
}

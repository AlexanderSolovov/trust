package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "parcel")
public class Parcel extends AbstractIdEntity {

    @Column(name = "land_type_code")
    private String landTypeCode;
    
    @Column
    private String uka;
    
    @Column(name = "survey_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date surveyDate;

    @Column(name = "hamlet_code")
    private String hamletCode;
    
    @Column
    private String address;
    
    @Column
    @ColumnTransformer(read = "st_astext(geom)", write = "st_geomfromtext(?, 4326)")
    private String geom;
    
    @Column
    private String comment;
    
    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false, updatable = false)
    private String endApplicationId;

    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;
        
    public Parcel() {
        super();
    }

    public String getLandTypeCode() {
        return landTypeCode;
    }

    public void setLandTypeCode(String landTypeCode) {
        this.landTypeCode = landTypeCode;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
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

    public String getEndApplicationId() {
        return endApplicationId;
    }

    public void setEndApplicationId(String endApplicationId) {
        this.endApplicationId = endApplicationId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
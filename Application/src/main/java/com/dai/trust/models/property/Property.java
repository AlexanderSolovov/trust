package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "property")
public class Property extends AbstractIdEntity {

    @Column(name = "parcel_id")
    private String parcelId;

    @Column(name = "file_number", insertable = false, updatable = false)
    private String fileNumber;

    @Column(name = "reg_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "prop_number", insertable = false, updatable = false)
    private String propNumber;

    @Column(name = "termination_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminationDate;

    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false, updatable = false)
    private String endApplicationId;

    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rrr> rights;

    public Property() {
        super();
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getPropNumber() {
        return propNumber;
    }

    public void setPropNumber(String propNumber) {
        this.propNumber = propNumber;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
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

    public List<Rrr> getRights() {
        return rights;
    }

    public void setRights(List<Rrr> rights) {
        this.rights = rights;
    }
}

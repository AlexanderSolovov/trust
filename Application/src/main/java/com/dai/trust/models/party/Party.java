package com.dai.trust.models.party;

import com.dai.trust.models.AbstractIdEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;

@Entity
@Inheritance
@DiscriminatorColumn(name="is_private")
@Table(name = "party")
public class Party extends AbstractIdEntity {

    @Column(name = "mobile_number")
    private String mobileNumber;
    
    @Column
    private String address;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private String parentId;

    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", insertable = false, updatable = false)
    private String endApplicationId;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyDocument> documents;
    
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;
    
    @Formula("check_party_editable(id) and status_code != 'historic'")
    private boolean editable;
    
    public Party() {
        super();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public List<PartyDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PartyDocument> documents) {
        this.documents = documents;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}

package com.dai.trust.models.property;

import com.dai.trust.models.AbstractIdEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
@Table(name = "rrr")
public class Rrr extends AbstractIdEntity {

    @Column(name = "property_id")
    @JsonIgnore
    private String propertyId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Property property;
    
    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "right_type_code")
    private String rightTypeCode;

    @Column
    private Double duration;

    @Column(name = "folio_number")
    private String folioNumber;

    @Column(name = "reg_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "start_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    @Column(name = "occupancy_type_code")
    private String occupancyTypeCode;

    @Column(name = "annual_fee")
    private Double annualFee;

    @Column(name = "juridical_area")
    private Double juridicalArea;

    @Column(name = "deal_amount")
    private Double dealAmount;

    @Column(name = "declared_landuse_code")
    private String declaredLanduseCode;

    @Column(name = "approved_landuse_code")
    private String approvedLanduseCode;

    @Column(name = "neighbor_north")
    private String neighborNorth;

    @Column(name = "neighbor_south")
    private String neighborSouth;

    @Column(name = "neighbor_east")
    private String neighborEast;

    @Column(name = "neighbor_west")
    private String neighborWest;

    @Column(name = "adjudicator1")
    private String adjudicator1;

    @Column(name = "adjudicator2")
    private String adjudicator2;

    @Column
    private String witness1;

    @Column
    private String witness2;

    @Column
    private String witness3;

    @Column(name = "allocation_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date allocationDate;

    @Column(name = "intereset_rate")
    private Double interesetRate;

    @Column
    private String description;

    @Column(name = "application_id", updatable = false)
    private String applicationId;

    @Column(name = "end_application_id", updatable = false, insertable = false)
    private String endApplicationId;

    @Column(name = "termination_application_id")
    private String terminationApplicationId;

    @Column(name = "termination_date", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminationDate;

    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;

    @OneToMany(mappedBy = "rrr", cascade = CascadeType.ALL, orphanRemoval = true)
    //@LazyCollection(LazyCollectionOption.FALSE)
    private List<Rightholder> rightholders;

    @OneToMany(mappedBy = "rrr", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Poi> pois;

    @OneToMany(mappedBy = "rrr", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeceasedOwner> deceasedOwners = new ArrayList<>();

    @OneToMany(mappedBy = "rrr", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RrrDocument> documents;

    @Transient
    private List<RrrLog> logs;
    
    public Rrr() {
        super();
    }

    public String getOccupancyTypeCode() {
        return occupancyTypeCode;
    }

    public void setOccupancyTypeCode(String occupancyTypeCode) {
        this.occupancyTypeCode = occupancyTypeCode;
    }

    public Double getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(Double annualFee) {
        this.annualFee = annualFee;
    }

    public Double getJuridicalArea() {
        return juridicalArea;
    }

    public void setJuridicalArea(Double juridicalArea) {
        this.juridicalArea = juridicalArea;
    }

    public Double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(Double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getDeclaredLanduseCode() {
        return declaredLanduseCode;
    }

    public void setDeclaredLanduseCode(String declaredLanduseCode) {
        this.declaredLanduseCode = declaredLanduseCode;
    }

    public String getApprovedLanduseCode() {
        return approvedLanduseCode;
    }

    public void setApprovedLanduseCode(String approvedLanduseCode) {
        this.approvedLanduseCode = approvedLanduseCode;
    }

    public String getNeighborNorth() {
        return neighborNorth;
    }

    public void setNeighborNorth(String neighborNorth) {
        this.neighborNorth = neighborNorth;
    }

    public String getNeighborSouth() {
        return neighborSouth;
    }

    public void setNeighborSouth(String neighborSouth) {
        this.neighborSouth = neighborSouth;
    }

    public String getNeighborEast() {
        return neighborEast;
    }

    public void setNeighborEast(String neighborEast) {
        this.neighborEast = neighborEast;
    }

    public String getNeighborWest() {
        return neighborWest;
    }

    public void setNeighborWest(String neighborWest) {
        this.neighborWest = neighborWest;
    }

    public String getAdjudicator1() {
        return adjudicator1;
    }

    public void setAdjudicator1(String adjudicator1) {
        this.adjudicator1 = adjudicator1;
    }

    public String getAdjudicator2() {
        return adjudicator2;
    }

    public void setAdjudicator2(String adjudicator2) {
        this.adjudicator2 = adjudicator2;
    }

    public String getWitness1() {
        return witness1;
    }

    public void setWitness1(String witness1) {
        this.witness1 = witness1;
    }

    public String getWitness2() {
        return witness2;
    }

    public void setWitness2(String witness2) {
        this.witness2 = witness2;
    }

    public String getWitness3() {
        return witness3;
    }

    public void setWitness3(String witness3) {
        this.witness3 = witness3;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRightTypeCode() {
        return rightTypeCode;
    }

    public void setRightTypeCode(String rightTypeCode) {
        this.rightTypeCode = rightTypeCode;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getInteresetRate() {
        return interesetRate;
    }

    public void setInteresetRate(Double interesetRate) {
        this.interesetRate = interesetRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTerminationApplicationId() {
        return terminationApplicationId;
    }

    public void setTerminationApplicationId(String terminationApplicationId) {
        this.terminationApplicationId = terminationApplicationId;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<Rightholder> getRightholders() {
        return rightholders;
    }

    public void setRightholders(List<Rightholder> rightholders) {
        this.rightholders = rightholders;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

    public DeceasedOwner getDeceasedOwner() {
        if (deceasedOwners == null || deceasedOwners.isEmpty()) {
            return null;
        }
        return deceasedOwners.get(0);
    }

    public void setDeceasedOwner(DeceasedOwner deceasedOwner) {
        if (deceasedOwners == null) {
            deceasedOwners = new ArrayList<>();
        }
        if (deceasedOwner != null) {
            if (deceasedOwners.isEmpty()) {
                deceasedOwners.add(deceasedOwner);
            } else {
                deceasedOwners.set(0, deceasedOwner);
            }
        } else {
            deceasedOwners.clear();
        }
    }

    public List<RrrDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<RrrDocument> documents) {
        this.documents = documents;
    }

    public List<RrrLog> getLogs() {
        return logs;
    }

    public void setLogs(List<RrrLog> logs) {
        this.logs = logs;
    }
}

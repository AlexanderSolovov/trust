package com.dai.trust.models.property;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;

@Entity
@DiscriminatorValue("ownership")
public class OwnershipRight extends Rrr {
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
    
    @Column(name = "allocation_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date allocationDate;
        
    public OwnershipRight() {
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

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }
}
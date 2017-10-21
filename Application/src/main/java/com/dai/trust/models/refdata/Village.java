package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_village")
@Entity
public class Village extends AbstractRefDataEntity {
    @Column(name = "district_code")
    private String districtCode;
    
    @Column
    private String address;
    
    @Column
    private String chairman;
    
    @Column(name = "executive_officer")
    private String executiveOfficer;
    
    public Village(){
        super();
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChairman() {
        return chairman;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }

    public String getExecutiveOfficer() {
        return executiveOfficer;
    }

    public void setExecutiveOfficer(String executiveOfficer) {
        this.executiveOfficer = executiveOfficer;
    }
}

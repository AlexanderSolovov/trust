package com.dai.trust.models.report;

import com.dai.trust.common.DateUtility;
import com.dai.trust.common.StringUtility;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "poi")
public class PoiSummary implements Serializable {

    @Id
    private String id;
    
    @Column(name = "rrr_id")
    private String rrrId;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "middle_name")
    private String middleName;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dob;

    @Column
    private String description;
        
    public PoiSummary() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getAge(){
        if(getDob() == null){
            return 0;
        }
        return DateUtility.getAge(getDob());
    }
    
    public String getFullName() {
        String name = "";
        if (!StringUtility.isEmpty(getFirstName())) {
            name = getFirstName().trim();
        }
        if (!StringUtility.isEmpty(getMiddleName())) {
            if (name.length() > 0) {
                name = name + " " + getMiddleName().trim();
            } else {
                name = getMiddleName().trim();
            }
        }
        if (!StringUtility.isEmpty(getLastName())) {
            if (name.length() > 0) {
                name = name + " " + getLastName().trim();
            } else {
                name = getLastName().trim();
            }
        }
        return name;
    }
}
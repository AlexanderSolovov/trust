package com.dai.trust.models.system;

import com.dai.trust.models.AbstractIdEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "appuser")
public class User extends AbstractIdEntity {

    @Column(name = "username")
    private String userName;

    @Column
    private String passwd;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column
    private boolean active;

    @Column
    private String description;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> groupCodes;
    
    public User() {
        super();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserGroup> getGroupCodes() {
        return groupCodes;
    }

    public void setGroupCodes(List<UserGroup> groupCodes) {
        this.groupCodes = groupCodes;
    }
    
    public String getFullName() {
        String fullName = getFirstName();
        if (getLastName() != null && !getLastName().equals("")) {
            if (fullName != null && !fullName.equals("")) {
                fullName = fullName + " " + getLastName();
            } else {
                fullName = getLastName();
            }
        }
        if (fullName == null || fullName.equals("")) {
            fullName = getUserName();
        }
        return fullName;
    }
}

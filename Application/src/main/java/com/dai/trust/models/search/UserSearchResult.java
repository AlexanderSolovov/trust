package com.dai.trust.models.search;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserSearchResult implements Serializable {

    @Id
    @Column(name = "username")
    private String userName;
    @Column(name = "full_name")
    private String fullName;
    
    public UserSearchResult() {
        super();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

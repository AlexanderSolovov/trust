package com.dai.trust.models.system;

import com.dai.trust.models.AbstractCodeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "approle")
public class AppRole extends AbstractCodeEntity {

    @Column(name = "role_name")
    private String roleName;

    @Column
    private String description;

    public AppRole() {
        super();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

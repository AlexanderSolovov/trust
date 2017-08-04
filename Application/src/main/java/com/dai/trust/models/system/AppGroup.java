package com.dai.trust.models.system;

import com.dai.trust.models.AbstractIdEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "appgroup")
public class AppGroup extends AbstractIdEntity {

    @Column(name = "group_name")
    private String groupName;

    @Column
    private String description;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppRoleGroup> roleCodes;

    public AppGroup() {
        super();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AppRoleGroup> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<AppRoleGroup> roleCodes) {
        this.roleCodes = roleCodes;
    }
}

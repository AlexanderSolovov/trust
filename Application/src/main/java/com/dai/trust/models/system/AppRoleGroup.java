package com.dai.trust.models.system;

import com.dai.trust.models.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "approle_appgroup")
@IdClass(AppRoleGroupId.class)
public class AppRoleGroup extends AbstractEntity {

    @Id
    @Column(name = "role_code", insertable = false, updatable = false)
    private String roleCode;

    @Id
    @JsonIgnore
    @Column(name = "group_id", insertable = false, updatable = false)
    private String groupId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", updatable = false, insertable = false, referencedColumnName = "id")
    private AppGroup group;

    public AppRoleGroup() {
        super();
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public AppGroup getGroup() {
        return group;
    }

    public void setGroup(AppGroup group) {
        this.group = group;
    }
}

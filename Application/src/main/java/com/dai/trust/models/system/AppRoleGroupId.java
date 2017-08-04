package com.dai.trust.models.system;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class AppRoleGroupId implements Serializable {
    private String roleCode;
    private String groupId;
    
    @Override
    public int hashCode() {
        return ((roleCode == null ? 0 : roleCode.hashCode()) + (groupId == null ? 0 : groupId.hashCode()));
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof AppRoleGroupId) {
            AppRoleGroupId other = (AppRoleGroupId) object;
            return (StringUtility.empty(other.getGroupId()).equals(StringUtility.empty(this.getGroupId())))
                    && (StringUtility.empty(other.getRoleCode()).equals(StringUtility.empty(this.getRoleCode())));
        }
        return false;
    }
}

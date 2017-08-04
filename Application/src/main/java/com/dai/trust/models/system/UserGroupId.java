package com.dai.trust.models.system;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class UserGroupId implements Serializable {
    private String userId;
    private String groupId;
    
    @Override
    public int hashCode() {
        return ((userId == null ? 0 : userId.hashCode()) + (groupId == null ? 0 : groupId.hashCode()));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof UserGroupId) {
            UserGroupId other = (UserGroupId) object;
            return (StringUtility.empty(other.getGroupId()).equals(StringUtility.empty(this.getGroupId())))
                    && (StringUtility.empty(other.getUserId()).equals(StringUtility.empty(this.getUserId())));
        }
        return false;
    }
}

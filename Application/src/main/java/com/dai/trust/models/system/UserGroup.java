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
@Table(name = "appuser_appgroup")
@IdClass(UserGroupId.class)
public class UserGroup extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;

    @Id
    @Column(name = "group_id", insertable = false, updatable = false)
    private String groupId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, referencedColumnName = "id")
    private User user;

    public UserGroup() {
        super();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

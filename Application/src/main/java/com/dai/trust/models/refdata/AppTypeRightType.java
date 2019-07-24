package com.dai.trust.models.refdata;

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
@Table(name = "ref_app_type_right_type")
@IdClass(AppTypeRightTypeId.class)
public class AppTypeRightType extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "app_type_code", insertable = false, updatable = false)
    private String appTypeCode;

    @Id
    @Column(name = "right_type_code", insertable = false, updatable = false)
    private String rightTypeCode;
    
    public AppTypeRightType() {
        super();
    }

    public String getAppTypeCode() {
        return appTypeCode;
    }

    public void setAppTypeCode(String appTypeCode) {
        this.appTypeCode = appTypeCode;
    }

    public String getRightTypeCode() {
        return rightTypeCode;
    }

    public void setRightTypeCode(String rightTypeCode) {
        this.rightTypeCode = rightTypeCode;
    }
}

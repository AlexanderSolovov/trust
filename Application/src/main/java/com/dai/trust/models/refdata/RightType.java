package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_right_type")
@Entity
public class RightType extends AbstractRefDataEntity {
    @Column(name = "right_type_group_code")
    private String rightTypeGroupCode;
    
    @Column(name = "allow_multiple")
    private boolean allowMultiple;
    
    public RightType(){
        super();
    }

    public String getRightTypeGroupCode() {
        return rightTypeGroupCode;
    }

    public void setRightTypeGroupCode(String rightTypeGroupCode) {
        this.rightTypeGroupCode = rightTypeGroupCode;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }
}

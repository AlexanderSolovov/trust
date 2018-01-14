package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_right_type_group")
@Entity
public class RightTypeGroup extends AbstractRefDataEntity {
    public static final String TYPE_OWNERSHIP = "ownership";
    public static final String TYPE_RESTRICTION = "restriction";
    
    public RightTypeGroup(){
        super();
    }
}

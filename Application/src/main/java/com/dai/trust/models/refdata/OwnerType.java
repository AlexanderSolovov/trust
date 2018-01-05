package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_owner_type")
@Entity
public class OwnerType extends AbstractRefDataEntity {
    public static final String TYPE_OWNER = "owner";
    public static final String TYPE_GUARDIAN = "guardian";
    public static final String TYPE_ADMINISTRATOR = "administrator";

    public OwnerType(){
        super();
    }
}

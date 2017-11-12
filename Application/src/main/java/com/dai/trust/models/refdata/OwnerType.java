package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_owner_type")
@Entity
public class OwnerType extends AbstractRefDataEntity {
    public OwnerType(){
        super();
    }
}

package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_land_type")
@Entity
public class LandType extends AbstractRefDataEntity {
    public LandType(){
        super();
    }
}

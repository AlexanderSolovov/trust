package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_landuse")
@Entity
public class LandUse extends AbstractRefDataEntity {
    public LandUse(){
        super();
    }
}

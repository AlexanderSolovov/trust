package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_region")
@Entity
public class Region extends AbstractRefDataEntity {
    public Region(){
        super();
    }
}

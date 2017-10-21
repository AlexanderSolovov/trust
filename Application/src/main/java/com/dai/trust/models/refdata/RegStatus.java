package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_reg_status")
@Entity
public class RegStatus extends AbstractRefDataEntity {
    public RegStatus(){
        super();
    }
}

package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_marital_status")
@Entity
public class MaritalStatus extends AbstractRefDataEntity {
    public MaritalStatus(){
        super();
    }
}

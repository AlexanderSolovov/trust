package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_id_type")
@Entity
public class IdType extends AbstractRefDataEntity {
    public IdType(){
        super();
    }
}

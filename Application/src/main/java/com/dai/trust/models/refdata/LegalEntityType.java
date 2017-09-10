package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_entity_type")
@Entity
public class LegalEntityType extends AbstractRefDataEntity {
    public LegalEntityType(){
        super();
    }
}

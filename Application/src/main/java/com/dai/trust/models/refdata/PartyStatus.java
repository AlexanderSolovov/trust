package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_party_status")
@Entity
public class PartyStatus extends AbstractRefDataEntity {
    public PartyStatus(){
        super();
    }
}

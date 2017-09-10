package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_citizenship")
@Entity
public class Citizenship extends AbstractRefDataEntity {
    public Citizenship(){
        super();
    }
}

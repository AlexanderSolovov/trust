package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_gender")
@Entity
public class Gender extends AbstractRefDataEntity {
    public Gender(){
        super();
    }
}

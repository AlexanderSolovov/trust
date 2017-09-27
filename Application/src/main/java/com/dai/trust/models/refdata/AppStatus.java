package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_app_status")
@Entity
public class AppStatus extends AbstractRefDataEntity {
    public AppStatus(){
        super();
    }
}

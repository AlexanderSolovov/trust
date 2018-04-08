package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_layer_type")
@Entity
public class LayerType extends AbstractRefDataEntity {
    public LayerType(){
        super();
    }
}

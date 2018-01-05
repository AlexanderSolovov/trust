package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_occupancy_type")
@Entity
public class OccupancyType extends AbstractRefDataEntity {
    public static final String TYPE_COMMON = "common";
    public static final String TYPE_SINGLE = "single";
    public static final String TYPE_GUARDIAN = "guardian";
    public static final String TYPE_JOINT = "joint";
    public static final String TYPE_NONNATURAL = "nonnatural";
    public static final String TYPE_PROBATE = "probate";

    public OccupancyType(){
        super();
    }
}

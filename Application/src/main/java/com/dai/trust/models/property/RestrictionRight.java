package com.dai.trust.models.property;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("restriction")
public class RestrictionRight extends Rrr {
    @Column(name = "intereset_rate")
    private Double interesetRate;

    public RestrictionRight() {
        super();
    }

    public Double getInteresetRate() {
        return interesetRate;
    }

    public void setInteresetRate(Double interesetRate) {
        this.interesetRate = interesetRate;
    }
}
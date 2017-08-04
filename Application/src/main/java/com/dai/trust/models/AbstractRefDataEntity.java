package com.dai.trust.models;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/** Abstract class for reference data entities. */
@MappedSuperclass
public abstract class AbstractRefDataEntity extends AbstractCodeEntity {
    @Column
    private String val;
    
    @Column
    private boolean active;

    public AbstractRefDataEntity(){
        super();
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package com.dai.trust.models;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/** Abstract class for entities with code field as a primary column */
@MappedSuperclass
public abstract class AbstractCodeEntity extends AbstractEntity {
    @Id
    @Column
    private String code;

    public AbstractCodeEntity(){
        super();
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

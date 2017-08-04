package com.dai.trust.models;

import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/** Abstract class for entities with id field as a primary column */
@MappedSuperclass
public abstract class AbstractIdEntity extends AbstractEntity {
    @Id
    private String id;

    public AbstractIdEntity(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public void preInsert(){
        if(getId() == null || getId().equals("")){
            setId(UUID.randomUUID().toString());
        }
        super.preInsert();
    }
}

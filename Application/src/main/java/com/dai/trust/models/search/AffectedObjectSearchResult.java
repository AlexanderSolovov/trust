package com.dai.trust.models.search;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AffectedObjectSearchResult implements Serializable {
    @Id
    private String id;
    @Column
    private String label;
    @Column(name ="object_type")
    private String objectType;
    @Column
    private String action;
        
    public static final String OBJECT_TYPE_PARCEL = "parcel";
    public static final String OBJECT_TYPE_PROPERTY = "prop";
    
    public AffectedObjectSearchResult(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

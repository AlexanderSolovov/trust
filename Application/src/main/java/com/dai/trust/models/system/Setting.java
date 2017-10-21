package com.dai.trust.models.system;

import com.dai.trust.models.AbstractIdEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "setting")
public class Setting extends AbstractIdEntity {
    public static final String SETTING_SRS = "srs";
    public static final String SETTING_OFFLINE_MODE = "offline-mode";
    public static final String SETTING_MAP_EXTENT = "map-extent";
    
    @Column
    private String val;

    @Column
    private boolean active;
    
    @Column
    private String description;

    public Setting() {
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
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

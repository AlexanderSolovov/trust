package com.dai.trust.models.system;

import com.dai.trust.models.AbstractIdEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "map_layer_option")
public class MapLayerOption extends AbstractIdEntity {

    @Column
    private String name;
    
    @Column
    private String val;
        
    @Column(name = "for_server")
    private boolean forServer;
    
    @JsonIgnore
    @Column(name = "layer_id")
    private String layerId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layer_id", updatable = false, insertable = false, referencedColumnName = "id")
    private MapLayer mapLayer;
    
    public MapLayerOption() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isForServer() {
        return forServer;
    }

    public void setForServer(boolean forServer) {
        this.forServer = forServer;
    }

    public String getLayerId() {
        return layerId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public MapLayer getMapLayer() {
        return mapLayer;
    }

    public void setMapLayer(MapLayer mapLayer) {
        this.mapLayer = mapLayer;
    }
}

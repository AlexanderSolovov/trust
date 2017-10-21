package com.dai.trust.models.system;

import java.io.Serializable;
import java.util.List;

/**
 * Various map settings
 */
public class MapSettings implements Serializable {
    private String printingSrs;
    private String mapExtent;
    private boolean offlineMode;
    private List<MapLayer> layers;
    
    public MapSettings(){
    }

    public String getPrintingSrs() {
        return printingSrs;
    }

    public void setPrintingSrs(String printingSrs) {
        this.printingSrs = printingSrs;
    }

    public String getMapExtent() {
        return mapExtent;
    }

    public void setMapExtent(String mapExtent) {
        this.mapExtent = mapExtent;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public List<MapLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<MapLayer> layers) {
        this.layers = layers;
    }
}

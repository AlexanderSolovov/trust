package com.dai.trust.services.system;

import com.dai.trust.models.system.MapLayer;
import com.dai.trust.models.system.MapSettings;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import java.util.List;
import javax.persistence.Query;

/**
 * Contains methods, related to managing map settings.
 */
public class MapService extends AbstractService {

    public MapService() {
        super();
    }
    /**
     * Returns active map layers.
     *
     * @return
     */
    public List<MapLayer> getActiveMapLayers() {
        Query q = getEM().createQuery("Select l From MapLayer l Where l.active = true Order by l.order", MapLayer.class);
        return q.getResultList();
    }

    /**
     * Returns all map layers.
     *
     * @return
     */
    public List<MapLayer> getAllMapLayers() {
        Query q = getEM().createQuery("Select l From MapLayer l Order by l.order", MapLayer.class);
        return q.getResultList();
    }
    
    /**
     * Returns map settings
     * @return 
     */
    public MapSettings getMapSettings() {
        Query q = getEM().createQuery("Select s From Setting s Where s.id in ('srs','map-extent','offline-mode')", Setting.class);
        List<Setting> settings = q.getResultList();
        MapSettings mapSetting = new MapSettings();

        if (settings != null && settings.size() > 0) {
            for (Setting setting : settings) {
                if (setting.getId().equalsIgnoreCase(Setting.SETTING_MAP_EXTENT)) {
                    mapSetting.setMapExtent(setting.getVal());
                } else if (setting.getId().equalsIgnoreCase(Setting.SETTING_OFFLINE_MODE)) {
                    mapSetting.setOfflineMode(setting.getVal().equals("1"));
                } else if (setting.getId().equalsIgnoreCase(Setting.SETTING_SRS)) {
                    mapSetting.setPrintingSrs(setting.getVal());
                }
            }
        }
        mapSetting.setLayers(getActiveMapLayers());
        return mapSetting;
    }
}

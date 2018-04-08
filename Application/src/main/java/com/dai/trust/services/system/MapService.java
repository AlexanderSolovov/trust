package com.dai.trust.services.system;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.system.MapLayer;
import com.dai.trust.models.system.MapLayerOption;
import com.dai.trust.models.system.MapSettings;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import java.util.List;
import java.util.UUID;
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
     *
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
                    // Get proj4 format
                    if (StringUtility.isInteger(setting.getVal())) {
                        q = getEM().createNativeQuery("select proj4text from public.spatial_ref_sys where auth_srid = :srid");
                        mapSetting.setPrintingSrsProj4((String) q.setParameter("srid", Integer.valueOf(setting.getVal())).getSingleResult());
                    }
                }
            }
        }
        mapSetting.setLayers(getActiveMapLayers());
        return mapSetting;
    }

    /**
     * Saves map layer to the database
     *
     * @param mapLayer Map layer to save
     * @return Returns saved map layer
     */
    public MapLayer saveMapLayer(MapLayer mapLayer) {
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();

        if (StringUtility.isEmpty(mapLayer.getId())) {
            mapLayer.setId(UUID.randomUUID().toString());
        }

        if (StringUtility.isEmpty(mapLayer.getName())) {
            errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_NAME_EMPTY));
        }

        if (StringUtility.isEmpty(mapLayer.getTitle())) {
            errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_TITLE_EMPTY));
        }

        if (StringUtility.isEmpty(mapLayer.getTypeCode())) {
            errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_TYPE_EMPTY));
        }

        if (StringUtility.isEmpty(mapLayer.getUrl())) {
            errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_URL_EMPTY));
        }

        if (mapLayer.getOptions() != null) {
            for (MapLayerOption option : mapLayer.getOptions()) {
                if (StringUtility.isEmpty(option.getName())) {
                    errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_OPTION_NAME_EMPTY));
                }
                if (StringUtility.isEmpty(option.getVal())) {
                    errors.addError(new TrustException(MessagesKeys.ERR_MAP_LAYER_VALUE_EMPTY));
                }
                if (StringUtility.isEmpty(option.getId())) {
                    option.setId(UUID.randomUUID().toString());
                }
                option.setLayerId(mapLayer.getId());
            }
        }

        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        return save(mapLayer, true);
    }
}

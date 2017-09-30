package com.dai.trust.services.system;

import com.dai.trust.models.system.MapLayer;
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
}

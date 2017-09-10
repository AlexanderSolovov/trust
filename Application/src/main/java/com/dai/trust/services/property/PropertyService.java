package com.dai.trust.services.property;

import com.dai.trust.models.property.Property;
import com.dai.trust.services.AbstractService;

/**
 * Contains methods for managing property objects.
 */
public class PropertyService extends AbstractService {

    public PropertyService() {
        super();
    }

    /**
     * Returns Property object.
     *
     * @param id Property id.
     * @return
     */
    public Property getProperty(String id) {
        return getById(Property.class, id, false);
    }
}

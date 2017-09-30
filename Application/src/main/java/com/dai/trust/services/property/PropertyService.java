package com.dai.trust.services.property;

import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.property.Property;
import com.dai.trust.services.AbstractService;
import java.util.List;
import javax.persistence.Query;

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
    
    /**
     * Returns Parcel by id.
     *
     * @param id Parcel id.
     * @return
     */
    public Parcel getParcel(String id) {
        return getById(Parcel.class, id, false);
    }
    
    /**
     * Returns Parcels by application id.
     *
     * @param id Application id.
     * @return
     */
    public List<Parcel> getParcelsByApplicationId(String id) {
        Query q = getEM().createQuery("Select p From Parcel p Where p.applicationId = :appId", Parcel.class);
        return q.setParameter("appId", id).getResultList();
    }
}

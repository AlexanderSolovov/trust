package com.dai.trust.services.property;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StatusCodeConstants;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.property.Property;
import com.dai.trust.models.search.ApplicationSearchResult;
import com.dai.trust.models.search.PropertyCodeSearchResult;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.search.SearchService;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityTransaction;
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
     * Returns Properties by application id.
     *
     * @param id Application id.
     * @return
     */
    public List<Property> getPropertiesByApplicationId(String id) {
        Query q = getEM().createQuery("Select p From Property p Where p.applicationId = :appId", Property.class);
        return q.setParameter("appId", id).getResultList();
    }
    
    /**
     * Returns Properties by Right id.
     *
     * @param id Right id.
     * @return
     */
    public Property getPropertyByRightId(String id) {
        Query q = getEM().createQuery("Select p From Property p join p.rights r Where r.id = :id", Property.class);
        return (Property) q.setParameter("id", id).setMaxResults(1).getSingleResult();
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

    /**
     * Saves provided parcels and returns them updated
     *
     * @param parcels List of parcels to save.
     * @param langCode Language code for localization
     * @return
     */
    public List<Parcel> saveParcels(List<Parcel> parcels, String langCode) {
        if (parcels == null || parcels.size() < 1) {
            return parcels;
        }

        // Make validations
        String appId = null;

        // Check provided parcels
        for (Parcel parcel : parcels) {
            if (StringUtility.isEmpty(parcel.getApplicationId())) {
                throw new TrustException(MessagesKeys.ERR_PARCEL_APPLICATION_EMPTY);
            }
            if (parcel.getSurveyDate() == null) {
                throw new TrustException(MessagesKeys.ERR_PARCEL_SURVEY_DATE_EMPTY);
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            if (parcel.getSurveyDate().after(cal.getTime())) {
                throw new TrustException(MessagesKeys.ERR_PARCEL_SURVEY_DATE_IN_FUTURE);
            }
            if (StringUtility.isEmpty(parcel.getHamletCode())) {
                throw new TrustException(MessagesKeys.ERR_PARCEL_HAMLET_EMPTY);
            }
            if (appId == null) {
                appId = parcel.getApplicationId();
            } else if (!appId.equalsIgnoreCase(parcel.getApplicationId())) {
                throw new TrustException(MessagesKeys.ERR_PARCEL_PARCELS_HAVE_DIFFERENT_APPLICATION);
            }
        }

        // Check application
        SearchService searchService = new SearchService();
        validateApplication(searchService.searchApplicationById(appId, appId));

        // Check number of parcel based on the application
        if(parcels.size() > 1){
            throw new TrustException(MessagesKeys.ERR_PARCEL_ONE_PARCEL_REQUIRED);
        }
        
        // Check parcels and save
        List<Parcel> dbParcels = getParcelsByApplicationId(appId);
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();
            if (dbParcels != null) {
                for (Parcel dbParcel : dbParcels) {
                    // Check status
                    if (!dbParcel.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
                        throw new TrustException(MessagesKeys.ERR_PARCEL_CANNOT_EDIT, new Object[]{dbParcel.getUka()});
                    }
                    // Check if parcel have to be deleted
                    boolean forDelete = true;
                    for (Parcel parcel : parcels) {
                        if (dbParcel.getId().equalsIgnoreCase(StringUtility.empty(parcel.getId()))) {
                            forDelete = false;
                            break;
                        }
                    }
                    if (forDelete) {
                        List<PropertyCodeSearchResult> propCodes = searchService.searchPropCodesByParcel(langCode, dbParcel.getId());
                        if (propCodes != null && propCodes.size() > 0) {
                            throw new TrustException(MessagesKeys.ERR_PARCEL_ATTACHED_CANNOT_DELETE, new Object[]{dbParcel.getUka(), propCodes.get(0).getPropNumber()});
                        }
                        // Delete
                        getEM().remove(dbParcel);
                    }
                }
            }

            // Save parcels
            for (Parcel parcel : parcels) {
                if (StringUtility.isEmpty(parcel.getId())) {
                    parcel.setId(UUID.randomUUID().toString());
                }
                parcel = getEM().merge(parcel);
            }

            // Commit changes
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }

        return getParcelsByApplicationId(appId);
    }

    public void validateApplication(ApplicationSearchResult app) {
        if (app == null) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_FOUND);
        }
        if (!app.getAssignee().equalsIgnoreCase(SharedData.getUserName())) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_ASSIGNED_TO_APPLICATION);
        }
        if (!app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            throw new TrustException(MessagesKeys.ERR_APP_READ_ONLY);
        }
    }
}

package com.dai.trust.services.property;

import com.dai.trust.common.DateUtility;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.RolesConstants;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StatusCodeConstants;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.application.Application;
import com.dai.trust.models.application.ApplicationProperty;
import com.dai.trust.models.party.PartyDocument;
import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.property.ParcelStatusChanger;
import com.dai.trust.models.property.Poi;
import com.dai.trust.models.property.Property;
import com.dai.trust.models.property.PropertyStatusChanger;
import com.dai.trust.models.property.Rightholder;
import com.dai.trust.models.property.Rrr;
import com.dai.trust.models.property.RrrDocument;
import com.dai.trust.models.property.RrrStatusChanger;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.AppTypeRightType;
import com.dai.trust.models.refdata.OccupancyType;
import com.dai.trust.models.refdata.OwnerType;
import com.dai.trust.models.refdata.RightType;
import com.dai.trust.models.refdata.TransactionType;
import com.dai.trust.models.search.ApplicationSearchResult;
import com.dai.trust.models.search.PropertyCodeSearchResult;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.application.ApplicationService;
import com.dai.trust.services.document.DocumentService;
import com.dai.trust.services.party.PartyService;
import com.dai.trust.services.refdata.RefDataService;
import com.dai.trust.services.search.SearchService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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
     * Returns Right by id.
     *
     * @param id Right id.
     * @return
     */
    public Rrr getRight(String id) {
        return getById(Rrr.class, id, false);
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
     * Returns Parcels by application id. If application for rectification, it
     * will also create a pending parcels to rectify.
     *
     * @param id Application id.
     * @return
     */
    public List<Parcel> getCreateParcelsByApplicationId(String id) {
        ApplicationService appService = new ApplicationService();
        Application app = appService.getApplication(id);
        List<Parcel> parcels = null;

        if (app != null) {
            Query q = getEM().createQuery("Select p From Parcel p Where p.applicationId = :appId", Parcel.class);
            parcels = q.setParameter("appId", id).getResultList();
            if (parcels == null) {
                parcels = new ArrayList<>();
            }

            RefDataService refService = new RefDataService();
            AppType appType = refService.getRefDataRecord(AppType.class, app.getAppTypeCode(), null);

            if (appType.getTransactionTypeCode().equals(TransactionType.RECTIFY)) {
                // Make copies of the property parcels attached to the application
                String sql = "select id, land_type_code, uka, survey_date, hamlet_code, address, comment, st_astext(geom) as geom, application_id, end_application_id, status_code, rowversion, action_code, action_user, action_time "
                        + "from public.parcel "
                        + "where id in (select pr.parcel_id from public.property pr inner join public.application_property ap on pr.id = ap.property_id where ap.app_id = :appId)";

                List<Parcel> appParcels = getEM().createNativeQuery(sql, Parcel.class).setParameter("appId", id).getResultList();

                if (app.getProperties() != null) {
                    for (Parcel appParcel : appParcels) {
                        // Check if parcel already in the list
                        boolean found = false;

                        for (Parcel parcel : parcels) {
                            if (StringUtility.empty(parcel.getUka()).equals(appParcel.getUka())) {
                                found = true;
                            }
                        }

                        if (!found && appParcel.getStatusCode().equals(StatusCodeConstants.ACTIVE)) {
                            // Reset version other system fields to make it as new
                            appParcel.setApplicationId(id);
                            appParcel.setVersion(0);
                            appParcel.setId(null);
                            appParcel.setStatusCode(StatusCodeConstants.PENDING);
                            parcels.add(appParcel);
                        }
                    }
                }
            }
        }
        return parcels;
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
        validateApplication(searchService.searchApplicationById(langCode, appId));

        // Check number of parcel based on the application
        if (parcels.size() > 1) {
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

    /**
     * Saves provided property and returns it updated
     *
     * @param prop Property object to save.
     * @param langCode Language code for localization
     * @return
     */
    public Property saveProperty(Property prop, String langCode) {
        if (prop == null) {
            return null;
        }

        boolean isNew = true;
        Property dbProp = null;

        // Valdate property and rights before saving
        if (!StringUtility.isEmpty(prop.getId())) {
            dbProp = getProperty(prop.getId());
            if (dbProp != null) {
                isNew = false;
                // Check it can be edited
                if (!canEditProp(dbProp)) {
                    throw new TrustException(MessagesKeys.ERR_PROP_READ_ONLY);
                }
            }
        }

        // Assign property id if missing
        if (StringUtility.isEmpty(prop.getId())) {
            prop.setId(UUID.randomUUID().toString());
        }

        // Check list of righs
        if (prop.getRights() == null || prop.getRights().size() < 1) {
            throw new TrustException(MessagesKeys.ERR_PROP_NO_RIGHTS_FOR_SAVE);
        }

        // Figure out application
        ApplicationSearchResult app = null;
        SearchService searchService = new SearchService();

        if (isNew || dbProp.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            // Verify application on property object and take it as current one
            if (dbProp != null && !StringUtility.isEmpty(dbProp.getApplicationId())) {
                app = searchService.searchApplicationById(langCode, dbProp.getApplicationId());
            } else if (!StringUtility.isEmpty(prop.getApplicationId())) {
                app = searchService.searchApplicationById(langCode, prop.getApplicationId());
            }
        } else // Search in the list of rights
        {
            if (prop.getRights() != null) {
                for (Rrr right : prop.getRights()) {
                    if ((StringUtility.isEmpty(right.getStatusCode()) || right.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING))
                            && !StringUtility.isEmpty(right.getApplicationId())) {
                        app = searchService.searchApplicationById(langCode, right.getApplicationId());
                        break;
                    } else if (!StringUtility.isEmpty(right.getTerminationApplicationId()) && StringUtility.empty(right.getStatusCode()).equalsIgnoreCase(StatusCodeConstants.CURRENT)) {
                        app = searchService.searchApplicationById(langCode, right.getTerminationApplicationId());
                        break;
                    }
                }
            }
        }

        // Validate application
        if (app == null) {
            throw new TrustException(MessagesKeys.ERR_PROP_APP_NOT_PROVIDED);
        }

        validateApplication(app);

        // Get application type
        RefDataService refService = new RefDataService();
        AppType appType = refService.getRefDataRecord(AppType.class, app.getAppTypeCode(), langCode);
        String transactionType = appType.getTransactionTypeCode();
        boolean isFirstReg = transactionType.equals(TransactionType.FIRST_REGISTRATION);

        // If first registration
        if (isFirstReg) {
            // Property must be pending and parcel not assigned to any other registered property
            if (dbProp != null && !dbProp.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
                throw new TrustException(MessagesKeys.ERR_PROP_MUST_BE_PEDNING);
            }

            if (StringUtility.isEmpty(prop.getParcelId())) {
                throw new TrustException(MessagesKeys.ERR_PROP_NO_PARCEL);
            }

            List<PropertyCodeSearchResult> parcelProps = searchService.searchPropCodesByParcel(langCode, prop.getParcelId());

            if (parcelProps != null) {
                for (PropertyCodeSearchResult propCode : parcelProps) {
                    if (!propCode.getId().equalsIgnoreCase(prop.getId()) && !propCode.getStatusCode().equalsIgnoreCase(StatusCodeConstants.HISTORIC)) {
                        throw new TrustException(MessagesKeys.ERR_PROP_PARCEL_ALREADY_INUSE, new Object[]{StringUtility.empty(propCode.getPropNumber())});
                    }
                }
            }

            // For first reg, make sure application id is assigned
            prop.setApplicationId(app.getId());
        } else {
            // db property must exists
            if (dbProp == null) {
                throw new TrustException(MessagesKeys.ERR_PROP_NOT_FOUND);
            }

            // If any post first registration transaction, validate list of attached properties to match with the current one
            List<PropertyCodeSearchResult> appProps = searchService.searchPropCodesByApplication(langCode, app.getId());
            boolean found = false;

            if (appProps != null) {
                for (PropertyCodeSearchResult propCode : appProps) {
                    if (propCode.getId().equalsIgnoreCase(prop.getId())) {
                        found = true;
                    }
                }
            }

            if (!found) {
                throw new TrustException(MessagesKeys.ERR_PROP_NOT_IN_APP);
            }
        }

        // Look for pending or for terminated rights based on allowed right types by application
        List<Rrr> rightsForSave = new ArrayList<>();
        for (Rrr right : prop.getRights()) {
            boolean isForTermination = !StringUtility.isEmpty(right.getTerminationApplicationId());

            // Only pending or rights for termination can be considered for saving
            if (StringUtility.isEmpty(right.getStatusCode())
                    || right.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)
                    || isForTermination) {

                // Check right type
                if (StringUtility.isEmpty(right.getRightTypeCode())) {
                    throw new TrustException(MessagesKeys.ERR_PROP_RIGHT_TYPE_EMPTY);
                }

                // Check if this right is allowed by application
                if (appType.getRightTypeCodes() != null) {
                    boolean allowed = false;
                    for (AppTypeRightType allowedRightType : appType.getRightTypeCodes()) {
                        if (allowedRightType.getRightTypeCode().equals(right.getRightTypeCode())) {
                            allowed = true;
                            break;
                        }
                    }
                    if (!allowed) {
                        throw new TrustException(MessagesKeys.ERR_PROP_FOUND_RIGHTS_NOT_FOR_SAVE);
                    }
                }

                // Assign id if empty
                if (StringUtility.isEmpty(right.getId())) {
                    right.setId(UUID.randomUUID().toString());
                }

                // Check against db rights
                Rrr dbRight = getRight(right.getId());
                if (dbRight != null) {
                    if (!dbRight.getPropertyId().equalsIgnoreCase(prop.getId())) {
                        throw new TrustException(MessagesKeys.ERR_PROP_RIGHT_HAS_DIFFERENT_PROP);
                    }
                    // If not termination, db right must be pending
                    if (!isForTermination && !dbRight.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
                        throw new TrustException(MessagesKeys.ERR_PROP_RIGHT_MUST_BE_PENDING);
                    }
                }

                // Check termination rule. Right must exist and have current status
                if (isForTermination && (dbRight == null || !dbRight.getStatusCode().equalsIgnoreCase(StatusCodeConstants.CURRENT))) {
                    throw new TrustException(MessagesKeys.ERR_PROP_RIGHT_MUST_BE_REGISTERED);
                }

                // Check for parent right to exist and has current status
                if (transactionType.equals(TransactionType.RECTIFY)
                        || transactionType.equals(TransactionType.VARY)
                        || transactionType.equals(TransactionType.TRANSFER)) {
                    if (StringUtility.isEmpty(right.getParentId())) {
                        throw new TrustException(MessagesKeys.ERR_PROP_PARENT_RIGHT_EMPTY);
                    }
                    // Check parent in DB. It must have current status
                    boolean parentOk = false;
                    if (dbProp != null && dbProp.getRights() != null) {
                        for (Rrr r : dbProp.getRights()) {
                            if (r.getId().equalsIgnoreCase(right.getParentId())) {
                                if (!r.getStatusCode().equalsIgnoreCase(StatusCodeConstants.CURRENT)) {
                                    throw new TrustException(MessagesKeys.ERR_PROP_PARENT_RIGHT_MUST_BE_REGISTERED);
                                }
                                parentOk = true;

                                // Copy over values from parent to make sure they are not modified
                                right.setRightTypeCode(r.getRightTypeCode());

                                if (transactionType.equals(TransactionType.TRANSFER)) {
                                    right.setDuration(r.getDuration());
                                    right.setAnnualFee(r.getAnnualFee());
                                    right.setApprovedLanduseCode(r.getApprovedLanduseCode());
                                    right.setStartDate(r.getStartDate());
                                    right.setEndDate(r.getEndDate());
                                }

                                if (transactionType.equals(TransactionType.VARY) || transactionType.equals(TransactionType.TRANSFER)) {
                                    right.setWitness1(r.getWitness1());
                                    right.setWitness2(r.getWitness2());
                                    right.setWitness3(r.getWitness3());
                                    right.setAllocationDate(r.getAllocationDate());
                                    right.setDeclaredLanduseCode(r.getDeclaredLanduseCode());
                                    right.setAdjudicator1(r.getAdjudicator1());
                                    right.setAdjudicator2(r.getAdjudicator2());
                                    right.setNeighborNorth(r.getNeighborNorth());
                                    right.setNeighborEast(r.getNeighborEast());
                                    right.setNeighborSouth(r.getNeighborSouth());
                                    right.setNeighborWest(r.getNeighborWest());
                                }
                                break;
                            }
                        }
                    }
                    if (!parentOk) {
                        throw new TrustException(MessagesKeys.ERR_PROP_PARENT_RIGHT_NOT_FOUND);
                    }
                } else {
                    // Set parent empty
                    right.setParentId(null);
                }

                // Set/overwrite right property id
                right.setPropertyId(prop.getId());

                // Do checks only for non termination case
                if (!isForTermination) {

                    // Assign application id
                    right.setApplicationId(app.getId());

                    // Check right attributes
                    // Check rightholders
                    if (right.getRightholders() == null || right.getRightholders().size() < 1) {
                        throw new TrustException(MessagesKeys.ERR_PROP_NO_RIGHTHOLDERS);
                    }

                    // Validate CCRO
                    if (right.getRightTypeCode().equalsIgnoreCase(RightType.TYPE_CCRO)) {
                        if (right.getAllocationDate() == null) {
                            throw new TrustException(MessagesKeys.ERR_PROP_ALLOCATION_DATE_EMPTY);
                        } else if (right.getAllocationDate().after(Calendar.getInstance().getTime())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_ALLOCATION_DATE_IN_FUTURE);
                        }

                        if (right.getStartDate() == null) {
                            throw new TrustException(MessagesKeys.ERR_PROP_START_DATE_EMPTY);
                        } else if (right.getStartDate().before(right.getAllocationDate())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_START_DATE_GREATER_ALLOCATION);
                        }

                        if (StringUtility.isEmpty(right.getDeclaredLanduseCode())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_DECLARED_LANDUSE_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getApprovedLanduseCode())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_APPROVED_LANDUSE_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getAdjudicator1())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_ADJUDICATOR1_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getAdjudicator2())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_ADJUDICATOR2_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getNeighborNorth())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_NORTH_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getNeighborSouth())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_SOUTH_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getNeighborEast())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_EAST_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getNeighborWest())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_WEST_EMPTY);
                        }
                        if (StringUtility.isEmpty(right.getOccupancyTypeCode())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_OCCUPANCY_TYPE_EMPTY);
                        }

                        // Check occupancy types
                        List<Rightholder> persons = filterRightholders(right.getRightholders(), true);
                        List<Rightholder> les = filterRightholders(right.getRightholders(), false);
                        String occupancyType = right.getOccupancyTypeCode();

                        // Age, share, type
                        int owners = 0;
                        int admins = 0;
                        int guardians = 0;
                        int minors = 0;

                        if (persons.size() > 0) {
                            for (Rightholder person : persons) {
                                if (person.getParty() == null) {
                                    throw new TrustException(MessagesKeys.ERR_PROP_NO_PARTY);
                                }

                                // Share
                                if (occupancyType.equals(OccupancyType.TYPE_COMMON) && StringUtility.isEmpty(person.getShareSize())) {
                                    throw new TrustException(MessagesKeys.ERR_PROP_SHARE_SIZE_EMPTY, new Object[]{person.getParty().getFullName()});
                                }

                                // Age
                                int age = DateUtility.getAge(person.getParty().getDob());
                                if (age < 18) {
                                    if (occupancyType.equals(OccupancyType.TYPE_GUARDIAN) && person.getOwnerTypeCode().equals(OwnerType.TYPE_OWNER)) {
                                        minors += 1;
                                    } else {
                                        throw new TrustException(MessagesKeys.ERR_PROP_YOUNG_OWNER, new Object[]{person.getParty().getFullName()});
                                    }
                                }

                                // Types
                                if (person.getOwnerTypeCode().equals(OwnerType.TYPE_GUARDIAN)) {
                                    guardians += 1;
                                }
                                if (person.getOwnerTypeCode().equals(OwnerType.TYPE_OWNER)) {
                                    owners += 1;
                                }
                                if (person.getOwnerTypeCode().equals(OwnerType.TYPE_ADMINISTRATOR)) {
                                    admins += 1;
                                }
                            }
                        }

                        // Owner types and number
                        if (occupancyType.equals(OccupancyType.TYPE_SINGLE)) {
                            if (owners < 1 || guardians > 0 || admins > 0) {
                                throw new TrustException(MessagesKeys.ERR_PROP_ONE_OWNER_ALLOWED);
                            }
                        }

                        if (occupancyType.equals(OccupancyType.TYPE_PROBATE)) {
                            if (admins < 1 || owners > 0 || guardians > 0) {
                                throw new TrustException(MessagesKeys.ERR_PROP_ONE_OR_MANY_ADMINS_ALLOWED);
                            }

                            if (right.getDeceasedOwner() == null) {
                                throw new TrustException(MessagesKeys.ERR_PROP_DP_EMPTY);
                            }
                            if (StringUtility.isEmpty(right.getDeceasedOwner().getFirstName())) {
                                throw new TrustException(MessagesKeys.ERR_PROP_DP_FIRST_NAME_EMPTY);
                            }
                            if (StringUtility.isEmpty(right.getDeceasedOwner().getLastName())) {
                                throw new TrustException(MessagesKeys.ERR_PROP_DP_LAST_NAME_EMPTY);
                            }

                            // Set right id
                            right.getDeceasedOwner().setRrrId(right.getId());
                        }

                        if (occupancyType.equals(OccupancyType.TYPE_COMMON) || occupancyType.equals(OccupancyType.TYPE_JOINT)) {
                            if (owners < 2 || guardians > 0 || admins > 0) {
                                throw new TrustException(MessagesKeys.ERR_PROP_MULTIPLE_OWNERS_REQUIRED);
                            }
                        }

                        if (occupancyType.equals(OccupancyType.TYPE_GUARDIAN)) {
                            if (owners < 1 || guardians < 1 || minors < 1 || admins > 0) {
                                throw new TrustException(MessagesKeys.ERR_PROP_WRONG_GUARDINASHIP);
                            }
                        }

                        if (occupancyType.equals(OccupancyType.TYPE_NONNATURAL)) {
                            if (les.size() < 1) {
                                throw new TrustException(MessagesKeys.ERR_PROP_LE_EMPTY);
                            } else if (les.size() > 1) {
                                throw new TrustException(MessagesKeys.ERR_PROP_ONE_LE_ALLOWED);
                            }
                        }
                    }

                    // Validate mortgage
                    if (right.getRightTypeCode().equalsIgnoreCase(RightType.TYPE_MORTGAGE)) {
                        // Set fields not allowed for mortgage to null
                        clearOwnershipFields(right);
                        right.setEndDate(null);

                        if (right.getStartDate() == null) {
                            throw new TrustException(MessagesKeys.ERR_PROP_START_DATE_EMPTY);
                        }
                        if (right.getDuration() == null || right.getDuration() <= 0) {
                            throw new TrustException(MessagesKeys.ERR_PROP_DURATION_EMPTY);
                        }

                        List<Rightholder> les = filterRightholders(right.getRightholders(), false);

                        if (les.size() < 1) {
                            throw new TrustException(MessagesKeys.ERR_PROP_LE_EMPTY);
                        }
                    }

                    // Validate caveat
                    if (right.getRightTypeCode().equalsIgnoreCase(RightType.TYPE_CAVEAT)) {
                        // Set fields not allowed for mortgage to null
                        clearOwnershipFields(right);
                        right.setDuration(null);
                        right.setInteresetRate(null);
                        right.setDealAmount(null);

                        if (right.getStartDate() == null) {
                            throw new TrustException(MessagesKeys.ERR_PROP_START_DATE_EMPTY);
                        }

                        if (right.getStartDate() != null && right.getEndDate() != null && right.getEndDate().before(right.getStartDate())) {
                            throw new TrustException(MessagesKeys.ERR_PROP_END_DATE_GREATER_START_DATE);
                        }
                    }

                    boolean strict = true;
                    if (transactionType.equals(TransactionType.RECTIFY)) {
                        // Allow rightholders editing
                        //strict = false;
                    }

                    // Validate rightholders
                    PartyService partyService = new PartyService();

                    for (Rightholder rightholder : right.getRightholders()) {
                        if (rightholder.getParty() == null) {
                            throw new TrustException(MessagesKeys.ERR_PROP_NO_PARTY);
                        }
                        if (rightholder.getParty().isIsPrivate()) {
                            partyService.validatePerson(rightholder.getParty(), langCode, strict);
                        } else {
                            partyService.validateLegalEntity(rightholder.getParty(), langCode, strict);
                        }
                    }

                    if (transactionType.equals(TransactionType.VARY)) {
                        // Make sure same rightholders provided on the right for variation
                        if (dbProp != null && dbProp.getRights() != null) {
                            for (Rrr r : dbProp.getRights()) {
                                if (r.getId().equalsIgnoreCase(right.getParentId())) {
                                    if (right.getRightholders().size() != r.getRightholders().size()) {
                                        throw new TrustException(MessagesKeys.ERR_PROP_RIGHTHOLDERS_MUST_BE_SAME);
                                    }
                                    for (Rightholder dbRh : r.getRightholders()) {
                                        boolean found = false;
                                        for (Rightholder rh : right.getRightholders()) {
                                            if (dbRh.getParty().getId().equals(StringUtility.empty(rh.getParty().getId()))) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            throw new TrustException(MessagesKeys.ERR_PROP_RIGHTHOLDERS_MUST_BE_SAME);
                                        }
                                    }

                                }
                            }
                        }
                    }

                    // Validate POIs
                    if (right.getPois() != null && right.getRightTypeCode().equalsIgnoreCase(RightType.TYPE_CCRO)) {
                        for (Poi poi : right.getPois()) {
                            if (StringUtility.isEmpty(poi.getFirstName())) {
                                throw new TrustException(MessagesKeys.ERR_PROP_POI_FIRST_NAME_EMPTY);
                            }
                            if (StringUtility.isEmpty(poi.getLastName())) {
                                throw new TrustException(MessagesKeys.ERR_PROP_POI_LAST_NAME_EMPTY);
                            }
                        }
                    }

                    // Validate right documents
                    if (right.getDocuments() != null) {
                        DocumentService docService = new DocumentService();
                        for (RrrDocument rrrDoc : right.getDocuments()) {
                            if (rrrDoc.getDocument() != null) {
                                docService.validateDocument(rrrDoc.getDocument(), strict);
                            }
                        }
                    }

                    // Check multiplicity 
                    RightType rightType = refService.getRefDataRecord(RightType.class, right.getRightTypeCode(), langCode);
                    if (rightType == null) {
                        throw new TrustException(MessagesKeys.ERR_PROP_RIGHT_TYPE_NOT_FOUND);
                    }

                    if (!rightType.isAllowMultiple() && rightsForSave.size() > 0) {
                        throw new TrustException(MessagesKeys.ERR_PROP_MULTIPLE_RIGHTS_NOT_ALLOWED, new Object[]{StringUtility.empty(rightType.getVal())});
                    }
                }

                // Assign party ids
                for (Rightholder rightholder : right.getRightholders()) {
                    if (rightholder.getParty() != null) {
                        rightholder.setRrrId(right.getId());
                        if (StringUtility.isEmpty(rightholder.getParty().getId())) {
                            rightholder.getParty().setId(UUID.randomUUID().toString());
                        }
                        rightholder.setPartyId(rightholder.getParty().getId());

                        // Assign id to party documents
                        if (rightholder.getParty().getDocuments() != null) {
                            for (PartyDocument partyDoc : rightholder.getParty().getDocuments()) {
                                partyDoc.setPartyId(rightholder.getParty().getId());
                                if (partyDoc.getDocument() != null) {
                                    // Assign id if missing
                                    if (StringUtility.isEmpty(partyDoc.getDocument().getId())) {
                                        partyDoc.getDocument().setId(UUID.randomUUID().toString());
                                    }
                                    partyDoc.setDocumentId(partyDoc.getDocument().getId());
                                }
                            }
                        }
                    }
                }

                // Assign document IDs
                if (right.getDocuments() != null) {
                    for (RrrDocument rrrDoc : right.getDocuments()) {
                        if (rrrDoc.getDocument() != null) {
                            // Assign id if missing
                            if (StringUtility.isEmpty(rrrDoc.getDocument().getId())) {
                                rrrDoc.getDocument().setId(UUID.randomUUID().toString());
                            }
                            rrrDoc.setDocumentId(rrrDoc.getDocument().getId());
                        }
                        // Assign right id to the document relation
                        rrrDoc.setRrrId(right.getId());
                    }
                }

                // Assign POI IDs
                if (right.getPois() != null) {
                    for (Poi poi : right.getPois()) {
                        // Assign id if missing
                        if (StringUtility.isEmpty(poi.getId())) {
                            poi.setId(UUID.randomUUID().toString());
                        }
                        // Assign right id
                        poi.setRrrId(right.getId());
                    }
                }

                // Add for saving
                rightsForSave.add(right);
            }
        }

        // Check list of rights for saving 
        if (rightsForSave.size() < 1) {
            throw new TrustException(MessagesKeys.ERR_PROP_NO_RIGHTS_FOR_SAVE);
        }

        // For first reggistration number of allowed rights for saving must match total number of rights on the property object
        if (isFirstReg && rightsForSave.size() != prop.getRights().size()) {
            throw new TrustException(MessagesKeys.ERR_PROP_FOUND_RIGHTS_NOT_FOR_SAVE);
        }

        // Do saving
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();

            // If first registration, save property first
            if (isFirstReg) {
                getEM().merge(prop);
            } else {
                for (Rrr right : rightsForSave) {
                    getEM().merge(right);
                }
                // Delete rights and reset for termination if not provided on the property object
                if (dbProp != null && dbProp.getRights() != null && dbProp.getRights().size() > 0) {
                    boolean hasChanges = false;
                    for (Iterator<Rrr> iter = dbProp.getRights().iterator(); iter.hasNext();) {
                        Rrr dbRight = iter.next();
                        if (dbRight.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                            // Check if right exists in the rights for saving
                            boolean found = false;
                            for (Rrr right : rightsForSave) {
                                if (dbRight.getId().equals(right.getId())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                iter.remove();
                                hasChanges = true;
                            }
                        } else if (dbRight.getTerminationApplicationId() != null && dbRight.getStatusCode().equals(StatusCodeConstants.CURRENT)) {
                            // Check for rights where for termination application removed
                            boolean found = false;
                            for (Rrr right : rightsForSave) {
                                if (dbRight.getId().equals(right.getId())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                dbRight.setTerminationApplicationId(null);
                                hasChanges = true;
                            }
                        }
                    }

                    if (hasChanges) {
                        getEM().merge(dbProp);
                    }
                }
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

        return getProperty(prop.getId());
    }

    private void clearOwnershipFields(Rrr right) {
        right.setWitness1(null);
        right.setWitness2(null);
        right.setWitness3(null);
        right.setAllocationDate(null);
        right.setAnnualFee(null);
        right.setDeclaredLanduseCode(null);
        right.setApprovedLanduseCode(null);
        right.setAdjudicator1(null);
        right.setAdjudicator2(null);
        right.setNeighborNorth(null);
        right.setNeighborEast(null);
        right.setNeighborSouth(null);
        right.setNeighborWest(null);
        right.setOccupancyTypeCode(null);
        right.setJuridicalArea(null);
        if (right.getPois() != null) {
            right.getPois().clear();
        } else {
            right.setPois(new ArrayList());
        }
        right.setDeceasedOwner(null);
    }

    private List<Rightholder> filterRightholders(List<Rightholder> rightholders, boolean isPerson) {
        List<Rightholder> result = new ArrayList<>();
        if (rightholders != null) {
            for (Rightholder rightholder : rightholders) {
                if (rightholder.getParty() != null) {
                    if ((isPerson && rightholder.getParty().isIsPrivate()) || (!isPerson && !rightholder.getParty().isIsPrivate())) {
                        result.add(rightholder);
                    }
                }
            }
        }
        return result;
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

    private boolean canEditProp(Property prop) {
        if (!StringUtility.isEmpty(prop.getStatusCode()) && prop.getStatusCode().equalsIgnoreCase(StatusCodeConstants.HISTORIC)) {
            return false;
        }
        return isInRole(RolesConstants.MANAGE_RIGHTS);
    }

    /**
     * Returns list of {@link ParcelStatusChanger} by application id.
     *
     * @param appId Application ID
     * @return
     */
    public List<ParcelStatusChanger> getParcelStatusChangersByApp(String appId) {
        Query q = getEM().createQuery("Select p From ParcelStatusChanger p Where p.applicationId = :appId", ParcelStatusChanger.class);
        return q.setParameter("appId", appId).getResultList();
    }

    /**
     * Returns {@link ParcelStatusChanger} by id.
     *
     * @param id Parcel ID
     * @return
     */
    public ParcelStatusChanger getParcelStatusChangerById(String id) {
        Query q = getEM().createQuery("Select p From ParcelStatusChanger p Where p.id = :id", ParcelStatusChanger.class);
        return (ParcelStatusChanger) q.setParameter("id", id).getSingleResult();
    }

    /**
     * Returns list of {@link PropertyStatusChanger} by application id.
     *
     * @param appId Application ID
     * @return
     */
    public List<PropertyStatusChanger> getPropertyStatusChangersByApp(String appId) {
        Query q = getEM().createQuery("Select p From PropertyStatusChanger p Where p.applicationId = :appId", PropertyStatusChanger.class);
        return q.setParameter("appId", appId).getResultList();
    }

    /**
     * Returns list of {@link PropertyStatusChanger} attached to the
     * application.
     *
     * @param appId Application ID
     * @return
     */
    public List<PropertyStatusChanger> getPropertyStatusChangersFromApp(String appId) {
        Query q = getEM().createNativeQuery("select id, prop_number, parcel_id, reg_date, termination_date, application_id, end_application_id, status_code, rowversion, action_code, action_user, action_time \n"
                + "from public.property where id in (select property_id from public.application_property where app_id = :appId)", PropertyStatusChanger.class);
        return q.setParameter("appId", appId).getResultList();
    }

    /**
     * Returns {@link PropertyStatusChanger} by id.
     *
     * @param id Property ID
     * @return
     */
    public PropertyStatusChanger getPropertyStatusChangersById(String id) {
        Query q = getEM().createQuery("Select p From PropertyStatusChanger p Where p.id = :id", PropertyStatusChanger.class);
        return (PropertyStatusChanger) q.setParameter("id", id).getSingleResult();
    }

    /**
     * Returns list of {@link RrrStatusChanger} by application id.
     *
     * @param appId Application ID
     * @return
     */
    public List<RrrStatusChanger> getRrrStatusChangersByApp(String appId) {
        Query q = getEM().createQuery("Select r From RrrStatusChanger r Where r.applicationId = :appId", RrrStatusChanger.class);
        return q.setParameter("appId", appId).getResultList();
    }

    /**
     * Returns list of {@link RrrStatusChanger} by property id.
     *
     * @param propId Property ID
     * @return
     */
    public List<RrrStatusChanger> getRrrStatusChangersByProp(String propId) {
        Query q = getEM().createQuery("Select r From RrrStatusChanger r Where r.propertyId = :propId", RrrStatusChanger.class);
        return q.setParameter("propId", propId).getResultList();
    }

    /**
     * Returns list of {@link RrrStatusChanger} by application for termination.
     *
     * @param appId Termination application ID
     * @return
     */
    public List<RrrStatusChanger> getRrrStatusChangersByTerminationApp(String appId) {
        Query q = getEM().createQuery("Select r From RrrStatusChanger r Where r.terminationApplicationId = :appId", RrrStatusChanger.class);
        return q.setParameter("appId", appId).getResultList();
    }

    /**
     * Returns {@link RrrStatusChanger} by RRR id.
     *
     * @param id RRR ID
     * @return
     */
    public RrrStatusChanger getRrrStatusChangerById(String id) {
        Query q = getEM().createQuery("Select r From RrrStatusChanger r Where r.id = :id", RrrStatusChanger.class);
        return (RrrStatusChanger) q.setParameter("id", id).getSingleResult();
    }
}

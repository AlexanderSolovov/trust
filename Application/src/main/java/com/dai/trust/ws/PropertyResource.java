package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.models.application.Application;
import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.property.Property;
import com.dai.trust.services.property.PropertyService;
import com.dai.trust.ws.filters.Authorized;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods to provide and manage reference data tables from the client.
 */
@Path("{langCode: [a-zA-Z]{2}}/property")
public class PropertyResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(PropertyResource.class.getName());

    public PropertyResource() {
        super();
    }

    /**
     * Returns Property by id.
     *
     * @param langCode Language code for localization
     * @param id Property id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getproperty|getProperty}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getProperty(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            PropertyService service = new PropertyService();
            return getMapper().writeValueAsString(service.getProperty(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns Property by Right id.
     *
     * @param langCode Language code for localization
     * @param id Right id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getpropertybyright|getPropertyByRight}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getPropertyByRight(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            PropertyService service = new PropertyService();
            return getMapper().writeValueAsString(service.getPropertyByRightId(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns properties by application id.
     *
     * @param langCode Language code for localization
     * @param appId Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getpropertiessbyapplication|getPropertiesByApplication}/{appId}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getPropertiesByApplication(@PathParam(value = LANG_CODE) String langCode, @PathParam("appId") String appId) {
        try {
            PropertyService service = new PropertyService();
            return getMapper().writeValueAsString(service.getPropertiesByApplicationId(appId));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns parcels by application id.
     *
     * @param langCode Language code for localization
     * @param appId Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getparcelsbyapplication|getParcelsByApplication}/{appId}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParcelsByApplication(@PathParam(value = LANG_CODE) String langCode, @PathParam("appId") String appId) {
        try {
            PropertyService service = new PropertyService();
            return getMapper().writeValueAsString(service.getParcelsByApplicationId(appId));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns parcel by id.
     *
     * @param langCode Language code for localization
     * @param id Parcel id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getparcel|getParcel}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParcel(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            PropertyService service = new PropertyService();
            return getMapper().writeValueAsString(service.getParcel(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Saves parcels and returns them updated.
     *
     * @param langCode Language code for localization
     * @param json Application object in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:saveparcels|saveParcels}")
    @Authorized(roles = RolesConstants.MANAGE_PARCELS)
    public String saveParcels(@PathParam(value = LANG_CODE) String langCode, String json) {
        try {
            PropertyService service = new PropertyService();
            List<Parcel> parcels;
            try {
                parcels = getMapper().readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, Parcel.class));
            } catch (Exception e) {
                logger.error("Failed to convert Parcels JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Parcel");
            }
            
            return getMapper().writeValueAsString(service.saveParcels(parcels, langCode));
            
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Saves property and returns it updated.
     *
     * @param langCode Language code for localization
     * @param json Property object in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:saveproperty|saveProperty}")
    @Authorized(roles = RolesConstants.MANAGE_RIGHTS)
    public String saveProperty(@PathParam(value = LANG_CODE) String langCode, String json) {
        try {
            PropertyService service = new PropertyService();
            Property prop;
            try {
                prop = getMapper().readValue(json, Property.class);
            } catch (Exception e) {
                logger.error("Failed to convert Property JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Property");
            }
            
            return getMapper().writeValueAsString(service.saveProperty(prop, langCode));
            
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

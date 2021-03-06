package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.models.search.ApplicationSearchParams;
import com.dai.trust.models.search.RightSearchParams;
import com.dai.trust.models.system.User;
import com.dai.trust.services.search.SearchService;
import com.dai.trust.services.system.UserService;
import com.dai.trust.ws.filters.Authorized;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods to provide and manage reference data tables from the client.
 */
@Path("{langCode: [a-zA-Z]{2}}/search")
public class SearchResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(SearchResource.class.getName());

    public SearchResource() {
        super();
    }

    /**
     * Searches private persons
     *
     * @param langCode Language code for localization
     * @param name Person name
     * @param idNumber Person ID number
     * @param ccro CCRO number
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "person")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchPerson(@PathParam(value = LANG_CODE) String langCode,
            @QueryParam(value = "name") String name,
            @QueryParam(value = "idnumber") String idNumber,
            @QueryParam(value = "ccro") String ccro) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchPerson(langCode, name, idNumber, ccro));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches legal entities
     *
     * @param langCode Language code for localization
     * @param name Legal entity name
     * @param regNumber Legal entity registration number
     * @param ccro CCRO number
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:legalentity|LegalEntity}")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchLegalEntity(@PathParam(value = LANG_CODE) String langCode,
            @QueryParam(value = "name") String name,
            @QueryParam(value = "regnumber") String regNumber,
            @QueryParam(value = "ccro") String ccro) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchLegalEntity(langCode, name, regNumber, ccro));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches current user applications
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:myapplications|myApplications}")
    @Authorized(roles = {RolesConstants.SEARCH, RolesConstants.VIEWING})
    public String searchMyApplications(@PathParam(value = LANG_CODE) String langCode) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchMyApplications(langCode));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches application by id
     *
     * @param langCode Language code for localization
     * @param id Application id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "application/{id}")
    @Authorized(roles = {RolesConstants.SEARCH, RolesConstants.VIEWING})
    public String searchApplicationById(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchApplicationById(langCode, id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches pending applications assigned to other users
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:pendingapplications|pendingApplications}")
    @Authorized(roles = {RolesConstants.SEARCH, RolesConstants.VIEWING})
    public String searchPendingApplications(@PathParam(value = LANG_CODE) String langCode) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchPendingApplications(langCode));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Search applications by different parameters.
     *
     * @param langCode Language code for localization
     * @param json Application search parameters in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "applications")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchApplications(@PathParam(value = LANG_CODE) String langCode, String json) {
        try {
            SearchService service = new SearchService();
            ApplicationSearchParams params = null;
            try {
                params = getMapper().readValue(json, ApplicationSearchParams.class);
            } catch (Exception e) {
                logger.error("Failed to convert ApplicationSearchParams JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Search");
            }
            
            return getMapper().writeValueAsString(service.searchApplications(langCode, params));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches for users with Viewing role and active status to use for
     * application assignment.
     * 
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:usersforassignment|usersForAssignment}")
    @Authorized(roles = {RolesConstants.ASSIGN_APPLICATIONS, RolesConstants.RE_ASSIGN_APPLICATIONS})
    public String searchUsersForAssignment(@PathParam(value = LANG_CODE) String langCode) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchUsersForAssignment());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches user by user name and returns user's full name
     *
     * @param langCode Language code for localization
     * @param username User name
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:userfullname|userFullName}/{username}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String searchUserFullName(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "username") String username) {
        try {
            UserService service = new UserService();
            User user = service.getUser(username);
            if(user != null){
                return getMapper().writeValueAsString(user.getFullName());
            }
            return "";
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches parcel by x, y coordinate
     *
     * @param langCode Language code for localization
     * @param x X coordinate
     * @param y Y coordinate
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:parcelbypoint|ParcelByPoint}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParcelByPoint(
            @PathParam(value = LANG_CODE) String langCode,
            @QueryParam(value = "x") String x,
            @QueryParam(value = "y") String y) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchParcelByPoint(langCode, x, y));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches parcels by application id
     *
     * @param langCode Language code for localization
     * @param appId Application id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:parcelsbyapplication|ParcelsByApplication}/{appId}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParcelsByApplication(
            @PathParam(value = LANG_CODE) String langCode,
            @PathParam("appId") String appId) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchParcelsByApplication(langCode, appId));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches parcel by its id
     *
     * @param langCode Language code for localization
     * @param id Parcel id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:parcelbyid|ParcelById}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParcelById(
            @PathParam(value = LANG_CODE) String langCode,
            @PathParam("id") String id) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchParcelById(langCode, id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches objects affected by application
     *
     * @param langCode Language code for localization
     * @param appId Application id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:affectedobjectsbyapplication|AffectedObjectsByApplication}/{appid}")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchAffectedObjectsByApplication(
            @PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "appid") String appId) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchAffectedObjects(langCode, appId));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Search property rights by different parameters.
     *
     * @param langCode Language code for localization
     * @param json Property right search parameters in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "rights")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchRights(@PathParam(value = LANG_CODE) String langCode, String json) {
        try {
            SearchService service = new SearchService();
            RightSearchParams params = null;
            try {
                params = getMapper().readValue(json, RightSearchParams.class);
            } catch (Exception e) {
                logger.error("Failed to convert RightSearchParams JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Search");
            }
            
            return getMapper().writeValueAsString(service.searchRights(langCode, params));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Searches property codes attached to the application
     *
     * @param langCode Language code for localization
     * @param appId Application id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:applicationproperties|ApplicationProperties}/{appId}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getApplicationProperties(
            @PathParam(value = LANG_CODE) String langCode,
            @PathParam("appId") String appId) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchPropCodesByApplication(langCode, appId));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.models.application.Application;
import com.dai.trust.models.search.ApplicationSearchParams;
import com.dai.trust.models.system.User;
import com.dai.trust.services.application.ApplicationService;
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
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "person")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchPerson(@PathParam(value = LANG_CODE) String langCode,
            @QueryParam(value = "name") String name,
            @QueryParam(value = "idnumber") String idNumber) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchPerson(langCode, name, idNumber));
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
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:legalentity|LegalEntity}")
    @Authorized(roles = RolesConstants.SEARCH)
    public String searchLegalEntity(@PathParam(value = LANG_CODE) String langCode,
            @QueryParam(value = "name") String name,
            @QueryParam(value = "regnumber") String regNumber) {
        try {
            SearchService service = new SearchService();
            return getMapper().writeValueAsString(service.searchLegalEntity(langCode, name, regNumber));
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
}

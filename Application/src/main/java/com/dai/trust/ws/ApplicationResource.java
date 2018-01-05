package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.models.application.Application;
import com.dai.trust.services.application.ApplicationService;
import com.dai.trust.ws.filters.Authorized;
import com.dai.trust.ws.responses.ResponseFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.ArrayList;
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
@Path("{langCode: [a-zA-Z]{2}}/application")
public class ApplicationResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(ApplicationResource.class.getName());

    public ApplicationResource() {
        super();
    }

    /**
     * Returns application by id.
     *
     * @param langCode Language code for localization
     * @param id Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getapplication|getApplication}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getApplication(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            ApplicationService service = new ApplicationService();
            return getMapper().writeValueAsString(service.getApplicationWithPermissions(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns application number by id.
     *
     * @param langCode Language code for localization
     * @param id Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getapplicationnumber|getApplicationNumber}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getApplicationNumber(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            ApplicationService service = new ApplicationService();
            return getMapper().writeValueAsString(service.getApplicationNumber(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Saves application and returns its id.
     *
     * @param langCode Language code for localization
     * @param json Application object in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:saveapplication|saveApplication}")
    @Authorized(roles = RolesConstants.MANAGE_APPLICATIONS)
    public String saveApplication(@PathParam(value = LANG_CODE) String langCode, String json) {
        try {
            ApplicationService service = new ApplicationService();
            Application app = null;
            try {
                app = getMapper().readValue(json, Application.class);
            } catch (Exception e) {
                logger.error("Failed to convert Application JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Application");
            }
            
            app = service.saveApplication(app, langCode);
            return String.format("{\"id\": \"%s\"}", app.getId());
            
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Approves application 
     *
     * @param langCode Language code for localization
     * @param id Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "approve/{id}")
    @Authorized(roles = RolesConstants.APPROVE_TRANSACTIONS)
    public String approveApplication(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            ApplicationService service = new ApplicationService();
            service.approveApplication(id);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Completes application 
     *
     * @param langCode Language code for localization
     * @param id Application id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "complete/{id}")
    @Authorized(roles = RolesConstants.MANAGE_APPLICATIONS)
    public String completeApplication(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            ApplicationService service = new ApplicationService();
            service.completeApplication(id);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Assigns applications and returns OK status.
     *
     * @param langCode Language code for localization
     * @param userName User name
     * @param json List of application ids to assign
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:assignapplications|assignApplications}/{userName}")
    @Authorized(roles = RolesConstants.ASSIGN_APPLICATIONS)
    public String assignApplications(@PathParam(value = LANG_CODE) String langCode, 
            @PathParam(value = "userName") String userName, String json) {
        try {
            ApplicationService service = new ApplicationService();
            List<String> ids = null;
            try {
                ids = getMapper().readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                logger.error("Failed to convert Application IDs JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Application");
            }
            
            service.assignApplications(ids, userName);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Assigns application and returns OK status.
     *
     * @param langCode Language code for localization
     * @param userName User name
     * @param id Application id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:assignapplication|assignApplication}/{id}/{userName}")
    @Authorized(roles = RolesConstants.ASSIGN_APPLICATIONS)
    public String assignApplication(@PathParam(value = LANG_CODE) String langCode, 
            @PathParam(value = "id") String id, @PathParam(value = "userName") String userName) {
        try {
            ApplicationService service = new ApplicationService();
            List<String> ids = new ArrayList<>();
            ids.add(id);
            
            service.assignApplications(ids, userName);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Rejects application and returns OK status if success.
     *
     * @param langCode Language code for localization
     * @param id Application ID
     * @param json Reason for rejection
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "reject/{id}")
    @Authorized(roles = RolesConstants.APPROVE_TRANSACTIONS)
    public String rejectApplication(@PathParam(value = LANG_CODE) String langCode, 
            @PathParam(value = "id") String id, String json) {
        try {
            ApplicationService service = new ApplicationService();
            String reason = "";
            try {
                reason = getMapper().readValue(json, String.class);
            } catch (Exception e) {
                logger.error("Failed to convert rejection reason JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Application");
            }
            service.rejectApplication(id, reason);
            
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Withdraws application and returns OK status if success.
     *
     * @param langCode Language code for localization
     * @param id Application ID
     * @param json Reason for withdrawal
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "withdraw/{id}")
    @Authorized(roles = RolesConstants.MANAGE_APPLICATIONS)
    public String withdrawApplication(@PathParam(value = LANG_CODE) String langCode, 
            @PathParam(value = "id") String id, String json) {
        try {
            ApplicationService service = new ApplicationService();
            String reason = "";
            try {
                reason = getMapper().readValue(json, String.class);
            } catch (Exception e) {
                logger.error("Failed to convert withdrawal reason JSON", e);
                throw ExceptionFactory.buildBadJson(langCode, "Application");
            }
            service.withdrawApplication(id, reason);
            
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.models.system.AppGroup;
import com.dai.trust.models.system.Setting;
import com.dai.trust.models.system.User;
import com.dai.trust.services.system.AppGroupService;
import com.dai.trust.services.system.AppRoleService;
import com.dai.trust.services.system.MapService;
import com.dai.trust.services.system.SettingsService;
import com.dai.trust.services.system.UserService;
import com.dai.trust.ws.filters.Authenticated;
import com.dai.trust.ws.filters.Authorized;
import com.dai.trust.ws.responses.ResponseFactory;
import javax.ws.rs.DELETE;
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
@Path("{langCode: [a-zA-Z]{2}}/sys")
public class SystemResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(SystemResource.class.getName());

    public SystemResource() {
        super();
    }

    /**
     * Returns all system settings
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getsettings|getSettings}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getSettings(@PathParam(value = LANG_CODE) String langCode) {
        try {
            SettingsService service = new SettingsService();
            return getMapper().writeValueAsString(service.getSettings());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns system setting by id
     *
     * @param langCode Language code for localization
     * @param id Setting id
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getsetting|getSetting}/{id}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getSetting(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "id") String id) {
        try {
            SettingsService service = new SettingsService();
            return getMapper().writeValueAsString(service.getSetting(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Saves setting
     *
     * @param langCode Language code for localization
     * @param json Setting in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:savesetting|saveSetting}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String saveSetting(@PathParam(value = LANG_CODE) String langCode, String json) {
        Setting setting = null;
        try {
            setting = getMapper().readValue(json, Setting.class);
        } catch (Exception e) {
            logger.error("Failed to convert Setting JSON", e);
            throw ExceptionFactory.buildBadJson(langCode, "Setting");
        }

        try {
            SettingsService service = new SettingsService();
            return getMapper().writeValueAsString(service.saveSetting(setting));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns application groups.
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getGroups|getgroups}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getGroups(@PathParam(value = LANG_CODE) String langCode) {
        try {
            AppGroupService service = new AppGroupService();
            return getMapper().writeValueAsString(service.getGroups());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Saves group
     *
     * @param langCode Language code for localization
     * @param json Group in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:savegroup|saveGroup}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String saveGroup(@PathParam(value = LANG_CODE) String langCode, String json) {
        AppGroup group = null;
        try {
            group = getMapper().readValue(json, AppGroup.class);
        } catch (Exception e) {
            logger.error("Failed to convert Group JSON", e);
            throw ExceptionFactory.buildBadJson(langCode, "Group");
        }

        try {
            AppGroupService service = new AppGroupService();
            return getMapper().writeValueAsString(service.saveGroup(group));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Deletes group
     *
     * @param langCode Language code for localization
     * @param id Group id
     * @return
     */
    @DELETE
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:deletegroup|deleteGroup}/{id}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String deleteGroup(@PathParam(value = LANG_CODE) String langCode, @PathParam(value = "id") String id) {
        try {
            AppGroupService service = new AppGroupService();
            service.deleteById(id, AppGroup.class);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns application groups.
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getRoles|getroles}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getRoles(@PathParam(value = LANG_CODE) String langCode) {
        try {
            AppRoleService service = new AppRoleService();
            return getMapper().writeValueAsString(service.getRoles());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns all users.
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getUsers|getusers}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getUsers(@PathParam(value = LANG_CODE) String langCode) {
        try {
            UserService service = new UserService();
            return getMapper().writeValueAsString(service.getUsers());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Saves user
     *
     * @param langCode Language code for localization
     * @param json User in JSON format
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:saveuser|saveUser}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String saveUser(@PathParam(value = LANG_CODE) String langCode, String json) {
        User user = null;
        try {
            user = getMapper().readValue(json, User.class);
        } catch (Exception e) {
            logger.error("Failed to convert User JSON", e);
            throw ExceptionFactory.buildBadJson(langCode, "User");
        }

        try {
            UserService service = new UserService();
            return getMapper().writeValueAsString(service.saveUser(user));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Deletes user
     *
     * @param langCode Language code for localization
     * @param id User id
     * @return
     */
    @DELETE
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:deleteuser|deleteUser}/{id}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String deleteUser(@PathParam(value = LANG_CODE) String langCode, @PathParam(value = "id") String id) {
        try {
            UserService service = new UserService();
            service.deleteById(id, User.class);
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns active map layers
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getactivemaplayers|getActiveMapLayers}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getActiveMapLayers(@PathParam(value = LANG_CODE) String langCode) {
        try {
            MapService service = new MapService();
            return getMapper().writeValueAsString(service.getActiveMapLayers());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns all map layers
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getallmaplayers|getAllMapLayers}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String getAllMapLayers(@PathParam(value = LANG_CODE) String langCode) {
        try {
            MapService service = new MapService();
            return getMapper().writeValueAsString(service.getAllMapLayers());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns map settings
     *
     * @param langCode Language code for localization
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getmapsettings|getMapSettings}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getMapSettings(@PathParam(value = LANG_CODE) String langCode) {
        try {
            MapService service = new MapService();
            return getMapper().writeValueAsString(service.getMapSettings());
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

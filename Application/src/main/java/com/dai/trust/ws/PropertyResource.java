package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.services.party.PartyService;
import com.dai.trust.services.property.PropertyService;
import com.dai.trust.ws.filters.Authorized;
import javax.ws.rs.GET;
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
}

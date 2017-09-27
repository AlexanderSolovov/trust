package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.services.party.PartyService;
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
@Path("{langCode: [a-zA-Z]{2}}/party")
public class PartyResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(PartyResource.class.getName());

    public PartyResource() {
        super();
    }

    /**
     * Returns private person by id.
     *
     * @param langCode Language code for localization
     * @param id Person id.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "{a:getparty|getParty}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public String getParty(@PathParam(value = LANG_CODE) String langCode, @PathParam("id") String id) {
        try {
            PartyService service = new PartyService();
            return getMapper().writeValueAsString(service.getParty(id));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

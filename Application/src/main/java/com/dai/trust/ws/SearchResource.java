package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.services.search.SearchService;
import com.dai.trust.ws.filters.Authorized;
import javax.ws.rs.GET;
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
}

package com.dai.trust.ws;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.RolesConstants;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.AbstractRefDataEntity;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.AppTypeGroup;
import com.dai.trust.models.refdata.Citizenship;
import com.dai.trust.models.refdata.DocumentType;
import com.dai.trust.models.refdata.Gender;
import com.dai.trust.models.refdata.IdType;
import com.dai.trust.models.refdata.Language;
import com.dai.trust.models.refdata.LegalEntityType;
import com.dai.trust.models.refdata.MaritalStatus;
import com.dai.trust.models.refdata.PartyStatus;
import com.dai.trust.services.refdata.RefDataService;
import com.dai.trust.ws.filters.Authenticated;
import com.dai.trust.ws.filters.Authorized;
import com.dai.trust.ws.responses.ResponseFactory;
import java.util.HashMap;
import javax.ws.rs.DELETE;
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
@Path("{langCode: [a-zA-Z]{2}}/ref")
public class RefDataResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(RefDataResource.class.getName());
    private final HashMap<String, Class<? extends AbstractRefDataEntity>> REF_DATA_CLASSES;

    public RefDataResource() {
        super();
        REF_DATA_CLASSES = new HashMap<>();
        REF_DATA_CLASSES.put(DocumentType.class.getSimpleName(), DocumentType.class);
        REF_DATA_CLASSES.put(Language.class.getSimpleName(), Language.class);
        REF_DATA_CLASSES.put(AppTypeGroup.class.getSimpleName(), AppTypeGroup.class);
        REF_DATA_CLASSES.put(AppType.class.getSimpleName(), AppType.class);
        REF_DATA_CLASSES.put(Gender.class.getSimpleName(), Gender.class);
        REF_DATA_CLASSES.put(IdType.class.getSimpleName(), IdType.class);
        REF_DATA_CLASSES.put(MaritalStatus.class.getSimpleName(), MaritalStatus.class);
        REF_DATA_CLASSES.put(PartyStatus.class.getSimpleName(), PartyStatus.class);
        REF_DATA_CLASSES.put(Citizenship.class.getSimpleName(), Citizenship.class);
        REF_DATA_CLASSES.put(LegalEntityType.class.getSimpleName(), LegalEntityType.class);
    }

    /**
     * Returns reference data table based on provided parameters.
     */
    private String getRefDataTable(String refType, String langCode, boolean onlyActive, boolean localized) {
        try {
            if (!REF_DATA_CLASSES.containsKey(StringUtility.empty(refType))) {
                throw new TrustException(MessagesKeys.ERR_REF_DATA_TYPE_NOT_FOUND, new Object[]{StringUtility.empty(refType)});
            }

            String localizationLang = localized ? langCode : null;
            RefDataService refService = new RefDataService();

            return getMapper().writeValueAsString(refService.getRefDataRecords(REF_DATA_CLASSES.get(refType), onlyActive, localizationLang));

        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Returns reference data item by its code.
     */
    public String getRefDataItem(String refType, String langCode, String code, boolean localized) {
        try {
            if (!REF_DATA_CLASSES.containsKey(StringUtility.empty(refType))) {
                throw new TrustException(MessagesKeys.ERR_REF_DATA_TYPE_NOT_FOUND, new Object[]{StringUtility.empty(refType)});
            }

            String localizationLang = localized ? langCode : null;
            RefDataService refService = new RefDataService();

            return getMapper().writeValueAsString(refService.getRefDataRecord(REF_DATA_CLASSES.get(refType), code, localizationLang));

        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Returns reference data records with active status
     *
     * @param langCode Language code for localization
     * @param refType Reference data type
     * @param unlocalized Indicates if value should not be localized and
     * returned with all languages. If this parameter is not specified, then
     * localized value will be returned using langCode parameter.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "getlist/{refType}")
    @Authenticated
    public String getActiveRecords(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "refType") String refType,
            @QueryParam(value = UNLOCALIZED) String unlocalized) {
        return getRefDataTable(refType, langCode, true, unlocalized == null);
    }

    /**
     * Returns all reference data records, active and non-active.
     *
     * @param langCode Language code for localization
     * @param refType Reference data type
     * @param unlocalized Indicates if value should not be localized and
     * returned with all languages. If this parameter is not specified, then
     * localized value will be returned using langCode parameter.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "getall/{refType}")
    @Authenticated
    public String getAllRecords(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "refType") String refType,
            @QueryParam(value = UNLOCALIZED) String unlocalized) {
        return getRefDataTable(refType, langCode, false, unlocalized == null);
    }
    
    /**
     * Returns reference data record by its code.
     *
     * @param langCode Language code for localization
     * @param refType Reference data type
     * @param code Reference record code
     * @param unlocalized Indicates if value should not be localized and
     * returned with all languages. If this parameter is not specified, then
     * localized value will be returned using langCode parameter.
     * @return
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    @Path(value = "get/{refType}/{code}")
    @Authenticated
    public String getRecord(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "refType") String refType,
            @PathParam(value = "code") String code,
            @QueryParam(value = UNLOCALIZED) String unlocalized) {
        return getRefDataItem(refType, langCode, code, unlocalized == null);
    }
    
    /**
     * Saves reference record
     *
     * @param langCode Language code for localization
     * @param refType Reference data type
     * @param json Reference record in JSON format, sent by the client.
     * @return
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Path(value = "save/{refType}")
    @Authorized(roles = RolesConstants.MANAGE_REF_DATA)
    public String saveRecord(@PathParam(value = LANG_CODE) String langCode, 
            @PathParam(value = "refType") String refType,
            String json) {
        AbstractRefDataEntity refDataRecord = null;
        try {
            if (!REF_DATA_CLASSES.containsKey(StringUtility.empty(refType))) {
                throw new TrustException(MessagesKeys.ERR_REF_DATA_TYPE_NOT_FOUND, new Object[]{StringUtility.empty(refType)});
            }
            refDataRecord = getMapper().readValue(json, REF_DATA_CLASSES.get(refType));
        } catch (Exception e) {
            logger.error("Failed to convert " + REF_DATA_CLASSES.get(refType).getName() + " JSON", e);
            throw ExceptionFactory.buildBadJson(langCode, REF_DATA_CLASSES.get(refType).getName());
        }

        try {
            RefDataService refService = new RefDataService();
            return getMapper().writeValueAsString(refService.saveRefDataREcord(REF_DATA_CLASSES.get(refType).cast(refDataRecord)));
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Deletes reference data record by its code.
     *
     * @param langCode Language code for localization
     * @param refType Reference data type
     * @param code Reference record code
     * @return
     */
    @DELETE
    @Produces("application/json; charset=UTF-8")
    @Path(value = "delete/{refType}/{code}")
    @Authorized(roles = RolesConstants.ADMIN)
    public String deleteRecord(@PathParam(value = LANG_CODE) String langCode,
            @PathParam(value = "refType") String refType,
            @PathParam(value = "code") String code) {
        try {
            if (!REF_DATA_CLASSES.containsKey(StringUtility.empty(refType))) {
                throw new TrustException(MessagesKeys.ERR_REF_DATA_TYPE_NOT_FOUND, new Object[]{StringUtility.empty(refType)});
            }
            RefDataService refService = new RefDataService();
            refService.deleteById(code, REF_DATA_CLASSES.get(refType));
            return ResponseFactory.buildOk();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}

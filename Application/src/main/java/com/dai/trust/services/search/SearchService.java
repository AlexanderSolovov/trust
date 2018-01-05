package com.dai.trust.services.search;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.models.search.AffectedObjectSearchResult;
import com.dai.trust.models.search.ApplicationNumberSearchResult;
import com.dai.trust.models.search.ApplicationSearchParams;
import com.dai.trust.models.search.ApplicationSearchResult;
import com.dai.trust.models.search.LegalEntitySearchResult;
import com.dai.trust.models.search.ParcelSearchResult;
import com.dai.trust.models.search.PersonSearchResult;
import com.dai.trust.models.search.PropertyCodeSearchResult;
import com.dai.trust.models.search.RightSearchParams;
import com.dai.trust.models.search.RightSearchResult;
import com.dai.trust.models.search.UserSearchResult;
import com.dai.trust.services.AbstractService;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * Contains various search methods.
 */
public class SearchService extends AbstractService {

    private final String APP_SEARCH_SELECT = "select a.id, a.app_type_code, get_translation(appt.val, :langCode) as app_type, a.app_number, a.lodgement_date, \n"
            + "	a.status_code, get_translation(apps.val, :langCode) as app_status, a.approve_reject_date, a.assignee, \n"
            + "	coalesce(u.first_name, '') || ' ' || coalesce(u.last_name, '') as assignee_name, a.assigned_on, \n"
            + "	applicants.applicant_data, props.ccros\n"
            + "from ((((public.application a inner join public.ref_app_type appt on a.app_type_code = appt.code) \n"
            + "	inner join public.ref_app_status apps on a.status_code = apps.code)\n"
            + "	left join public.appuser u on a.assignee = u.username)\n"
            + "	inner join (\n"
            + "		select ap.app_id, string_agg(trim(trim(p.name1 || ' ' || coalesce(p.name3, '')) || ' ' || coalesce(p.name2, '')), ', ') as applicant_names,\n"
            + "		  string_agg(coalesce(p.id_number, ''), ',') as applicant_ids,\n"
            + "		  string_agg(trim(trim(p.name1 || ' ' || coalesce(p.name3, '')) || ' ' || coalesce(p.name2, '')) || \n"
            + "		  (case when coalesce(p.id_number, '') != '' then E'\\n' || '(' || trim(coalesce(get_translation(idt.val, :langCode), '') || ' #' || p.id_number) || ')' else '' end), E',\\n' order by p.is_private, p.name1) as applicant_data \n"
            + "		from public.application_party ap inner join public.party p on ap.party_id = p.id left join public.ref_id_type idt on p.id_type_code = idt.code\n"
            + "		group by ap.app_id) applicants on a.id = applicants.app_id)\n"
            + "	left join (\n"
            + "		select ap.app_id, string_agg(p.prop_number, ', ') as ccros \n"
            + "		from public.application_property ap inner join property p on ap.property_id = p.id \n"
            + "		group by ap.app_id) props on a.id = props.app_id ";

    private final String APP_SEARCH_ORDER = " order by a.lodgement_date desc ";

    private final String PARCEL_SEARCH_SELECT = "select p.id, p.land_type_code, get_translation(lt.val, :langCode) as land_type_name,"
            + "	p.uka, st_astext(p.geom) as geom, p.survey_date, p.hamlet_code, p.address, "
            + "	public.get_location_by_hamlet(p.hamlet_code, :langCode) as parcel_location, "
            + "	p.comment, p.application_id, a.app_number, p.end_application_id, "
            + "	aend.app_number as end_app_number, p.status_code, "
            + "	get_translation(ps.val, :langCode) as status_name "
            + "from (((public.parcel p left join public.ref_land_type lt on p.land_type_code = lt.code) "
            + "        inner join public.ref_parcel_status ps on p.status_code = ps.code) "
            + "        left join public.application a on p.application_id = a.id) "
            + "        left join public.application aend on p.end_application_id = aend.id";

    private final String PROP_CODE_SEARCH_SELECT = "select p.id, p.prop_number, p.status_code, get_translation(rs.val, :langCode) as status_name "
            + "from public.property p inner join public.ref_reg_status rs on p.status_code = rs.code";

    private final String APP_NUMBER_SEARCH_SELECT = "select id, app_type_code, app_number, lodgement_date, \n"
            + "	status_code, approve_reject_date, assignee from public.application \n";

    private final String AFFECTED_OBJECTS_SEARCH_SELECT = "select distinct p.id, p.prop_number as label, 'prop' as object_type, \n"
            + " (case \n"
            + "   when p.application_id = :appId then 'created' \n"
            + "   when p.end_application_id = :appId then 'terminated' \n"
            + "   else 'modified' end) as action \n"
            + " from public.property p left join public.rrr r on p.id = r.property_id \n"
            + " where p.application_id = :appId or p.end_application_id = :appId or \n"
            + " r.application_id = :appId or r.end_application_id = :appId \n"
            + "union\n"
            + "select id, uka as label, 'parcel' as object_type, (case when application_id = :appId then 'created' else 'terminated' end) as action \n"
            + " from public.parcel \n"
            + " where application_id = :appId or end_application_id = :appId \n"
            + "order by object_type";

    public SearchService() {
        super();
    }

    /**
     * Searches objects affected by application.
     *
     * @param langCode Language code
     * @param appId Application id
     * @return
     */
    public List<AffectedObjectSearchResult> searchAffectedObjects(String langCode, String appId) {
        Query q = getEM().createNativeQuery(AFFECTED_OBJECTS_SEARCH_SELECT, AffectedObjectSearchResult.class);
        q.setParameter("appId", appId);
        List<AffectedObjectSearchResult> result = q.getResultList();
        
        // Localize actions
        if(result != null){
            MessageProvider msgProvider = new MessageProvider(langCode);
            for(AffectedObjectSearchResult obj : result){
                obj.setAction(msgProvider.getMessage("GENERAL_" + obj.getAction().toUpperCase()));
            }
        }
        return result;
    }
    
    /**
     * Searches for current user applications.
     *
     * @param langCode Language code for localization
     * @return
     */
    public List<ApplicationSearchResult> searchMyApplications(String langCode) {
        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(APP_SEARCH_SELECT
                + "\n where a.complete_date is null and a.assignee = :username"
                + APP_SEARCH_ORDER
                + " limit 1000", ApplicationSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("username", SharedData.getUserName());
        return q.getResultList();
    }

    /**
     * Searches application numbers by property id.
     *
     * @param propId Property id
     * @return
     */
    public List<ApplicationNumberSearchResult> searchAppNumbersByProp(String propId) {
        Query q = getEM().createNativeQuery(APP_NUMBER_SEARCH_SELECT
                + " where id in (select app_id from public.application_property where property_id = :propId)", ApplicationNumberSearchResult.class);
        q.setParameter("propId", propId);
        return q.getResultList();
    }

    /**
     * Searches pending applications assigned to the other users.
     *
     * @param langCode Language code for localization
     * @return
     */
    public List<ApplicationSearchResult> searchPendingApplications(String langCode) {
        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(APP_SEARCH_SELECT
                + "\n where a.status_code = 'pending' and a.assignee != :username"
                + APP_SEARCH_ORDER
                + " limit 1000", ApplicationSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("username", SharedData.getUserName());
        return q.getResultList();
    }

    /**
     * Searches application by id.
     *
     * @param langCode Language code for localization
     * @param appId
     * @return
     */
    public ApplicationSearchResult searchApplicationById(String langCode, String appId) {
        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(APP_SEARCH_SELECT + " where a.id = :appId", ApplicationSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("appId", appId);
        return (ApplicationSearchResult) q.getSingleResult();
    }

    /**
     * Searches applications by various parameters.
     *
     * @param langCode Language code for localization
     * @param params Application search parameters object
     * @return
     */
    public List<ApplicationSearchResult> searchApplications(String langCode, ApplicationSearchParams params) {
        if (params == null) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        if (StringUtility.isEmpty(params.getNumber())) {
            params.setNumber("%");
        } else {
            params.setNumber("%" + params.getNumber().trim().toLowerCase() + "%");
        }

        if (params.getTypeCode() == null) {
            params.setTypeCode("");
        }

        if (StringUtility.isEmpty(params.getApplicantName())) {
            params.setApplicantName("%");
        } else {
            params.setApplicantName("%" + params.getApplicantName().trim().toLowerCase() + "%");
        }

        if (StringUtility.isEmpty(params.getApplicantIdNumber())) {
            params.setApplicantIdNumber("%");
        } else {
            params.setApplicantIdNumber("%" + params.getApplicantIdNumber().trim().toLowerCase() + "%");
        }

        if (params.getStatusCode() == null) {
            params.setStatusCode("");
        }

        if (StringUtility.isEmpty(params.getCcroNumber())) {
            params.setCcroNumber("%");
        } else {
            params.setCcroNumber("%" + params.getCcroNumber().trim().toLowerCase() + "%");
        }

        if (params.getLodgemenetDateFrom() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(params.getLodgemenetDateFrom());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            params.setLodgemenetDateFrom(cal.getTime());
            if (params.getLodgemenetDateTo() == null) {
                cal.setTime(params.getLodgemenetDateFrom());
                cal.add(Calendar.YEAR, 100);
                params.setLodgemenetDateTo(cal.getTime());
            }
        }

        if (params.getLodgemenetDateTo() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(params.getLodgemenetDateTo());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            params.setLodgemenetDateTo(cal.getTime());
            if (params.getLodgemenetDateFrom() == null) {
                cal.setTime(params.getLodgemenetDateTo());
                cal.add(Calendar.YEAR, -100);
                params.setLodgemenetDateFrom(cal.getTime());
            }
        }

        Query q = getEM().createNativeQuery(APP_SEARCH_SELECT
                + "\n where lower(a.app_number) like :number "
                + "and ('' = :typeCode or a.app_type_code = :typeCode) "
                + "and lower(applicants.applicant_names) like :applicantName "
                + "and lower(applicants.applicant_ids) like :applicantIdNumber "
                + "and ('' = :statusCode or a.status_code = :statusCode) "
                + "and lower(coalesce(props.ccros, '')) like :ccroNumber "
                + "and (cast(:lodgementDateFrom as date) is null or a.lodgement_date between :lodgementDateFrom and :lodgementDateTo) "
                + "order by a.lodgement_date limit 1001;", ApplicationSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("number", params.getNumber());
        q.setParameter("typeCode", params.getTypeCode());
        q.setParameter("applicantName", params.getApplicantName());
        q.setParameter("applicantIdNumber", params.getApplicantIdNumber());
        q.setParameter("statusCode", params.getStatusCode());
        q.setParameter("ccroNumber", params.getCcroNumber());
        q.setParameter("lodgementDateFrom", params.getLodgemenetDateFrom(), TemporalType.TIMESTAMP);
        q.setParameter("lodgementDateTo", params.getLodgemenetDateTo(), TemporalType.TIMESTAMP);

        return q.getResultList();
    }

    /**
     * Searches property right by various parameters.
     *
     * @param langCode Language code for localization
     * @param params Right search parameters object
     * @return
     */
    public List<RightSearchResult> searchRights(String langCode, RightSearchParams params) {
        if (params == null) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        if (StringUtility.isEmpty(params.getPropNumber())) {
            params.setPropNumber("%");
        } else {
            params.setPropNumber("%" + params.getPropNumber().trim().toLowerCase() + "%");
        }
        
        if (StringUtility.isEmpty(params.getFileNumber())) {
            params.setFileNumber("%");
        } else {
            params.setFileNumber("%" + params.getFileNumber().trim().toLowerCase() + "%");
        }

        if (params.getRightTypeCode()== null) {
            params.setRightTypeCode("");
        }

        if (StringUtility.isEmpty(params.getRightholderName())) {
            params.setRightholderName("%");
        } else {
            params.setRightholderName("%" + params.getRightholderName().trim().toLowerCase() + "%");
        }

        if (StringUtility.isEmpty(params.getRightholderIdNumber())) {
            params.setRightholderIdNumber("%");
        } else {
            params.setRightholderIdNumber("%" + params.getRightholderIdNumber().trim().toLowerCase() + "%");
        }

        if (params.getStatusCode() == null) {
            params.setStatusCode("");
        }

        if (StringUtility.isEmpty(params.getUkaNumber())) {
            params.setUkaNumber("%");
        } else {
            params.setUkaNumber("%" + params.getUkaNumber().trim().toLowerCase() + "%");
        }

        Query q = getEM().createNativeQuery(RightSearchResult.QUERY_SEARCH, RightSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter(RightSearchResult.PARAM_FILE_NUMBER, params.getFileNumber());
        q.setParameter(RightSearchResult.PARAM_PROP_NUMBER, params.getPropNumber());
        q.setParameter(RightSearchResult.PARAM_RIGHTHOLDER_ID, params.getRightholderIdNumber());
        q.setParameter(RightSearchResult.PARAM_RIGHTHOLDER_NAME, params.getRightholderName());
        q.setParameter(RightSearchResult.PARAM_RIGHT_TYPE_CODE, params.getRightTypeCode());
        q.setParameter(RightSearchResult.PARAM_STATUS_CODE, params.getStatusCode());
        q.setParameter(RightSearchResult.PARAM_UKA, params.getUkaNumber());

        return q.getResultList();
    }
    
    /**
     * Searches for party by name and id number.
     *
     * @param langCode Language code for localization
     * @param name Person name
     * @param idNumber Person ID number
     * @return
     */
    public List<PersonSearchResult> searchPerson(String langCode, String name, String idNumber) {
        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }
        if (StringUtility.isEmpty(name)) {
            name = "%";
        } else {
            name = "%" + name.toLowerCase().trim().replace(" ", "%") + "%";
        }
        if (StringUtility.isEmpty(idNumber)) {
            idNumber = "%";
        } else {
            idNumber = "%" + idNumber.toLowerCase().trim() + "%";
        }

        Query q = getEM().createNativeQuery("select p.id, get_translation(idt.val, :langCode) as id_type, (name1 || ' ' || coalesce(name3, '') || ' ' || coalesce(name2, '')) as name, p.id_number, p.dob, p.mobile_number, p.address, p.status_code\n "
                + "from party p inner join ref_id_type idt on p.id_type_code = idt.code\n "
                + "where p.is_private = 't' and "
                + "lower(name1 || ' ' || coalesce(name3, '') || ' ' || coalesce(name2, '')) like :name and "
                + "lower(coalesce(p.id_number, '')) like :idNumber order by name1, name2 limit 1000", PersonSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("name", name);
        q.setParameter("idNumber", idNumber);
        return q.getResultList();
    }

    /**
     * Searches for legal entity by name and registration number.
     *
     * @param langCode Language code for localization
     * @param name Legal entity name
     * @param regNumber Legal entity registration number
     * @return
     */
    public List<LegalEntitySearchResult> searchLegalEntity(String langCode, String name, String regNumber) {
        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }
        if (StringUtility.isEmpty(name)) {
            name = "%";
        } else {
            name = "%" + name.toLowerCase().trim() + "%";
        }
        if (StringUtility.isEmpty(regNumber)) {
            regNumber = "%";
        } else {
            regNumber = "%" + regNumber.toLowerCase().trim() + "%";
        }

        Query q = getEM().createNativeQuery("select p.id, get_translation(et.val, :langCode) as entity_type, name1 as name, p.id_number as reg_number, p.dob as establishment_date, p.mobile_number, p.address, p.status_code\n "
                + "from party p inner join ref_entity_type et on p.entity_type_code = et.code\n "
                + "where p.is_private = 'f' and "
                + "lower(name1) like :name and "
                + "lower(coalesce(p.id_number, '')) like :regNumber order by name1 limit 1000", LegalEntitySearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("name", name);
        q.setParameter("regNumber", regNumber);
        return q.getResultList();
    }

    /**
     * Searches for users with Viewing role and active status to use for
     * application assignment.
     *
     * @return
     */
    public List<UserSearchResult> searchUsersForAssignment() {
        Query q = getEM().createNativeQuery("select u.username, (u.first_name || ' ' || u.last_name) as full_name "
                + "from public.appuser u inner join public.user_role ur on u.username = ur.username "
                + "where u.active = 't' and ur.rolename = 'Viewing'"
                + "order by u.first_name", UserSearchResult.class);
        return q.getResultList();
    }

    /**
     * Searches parcel by x, y coordinate.
     *
     * @param langCode Language code for localization
     * @param x X coordinate
     * @param y Y coordinate
     * @return
     */
    public ParcelSearchResult searchParcelByPoint(String langCode, String x, String y) {
        if (StringUtility.isEmpty(x) || StringUtility.isEmpty(y)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }
        String point = "POINT(" + x + " " + y + ")";

        Query q = getEM().createNativeQuery(PARCEL_SEARCH_SELECT
                + " where ST_Contains(p.geom, ST_GeomFromText(:wktPoint, St_SRID(p.geom))) order by p.survey_date desc",
                ParcelSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("wktPoint", point);
        List<ParcelSearchResult> results = q.getResultList();

        if (results != null && results.size() > 0) {
            results.get(0).setPropCodes(searchPropCodesByParcel(langCode, results.get(0).getId()));
            return results.get(0);
        }
        return null;
    }

    /**
     * Searches parcels by application.
     *
     * @param langCode Language code for localization
     * @param appId Application ID
     * @return
     */
    public List<ParcelSearchResult> searchParcelsByApplication(String langCode, String appId) {
        if (StringUtility.isEmpty(appId)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PARCEL_SEARCH_SELECT
                + " where p.application_id = :appId order by p.survey_date desc",
                ParcelSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("appId", appId);
        List<ParcelSearchResult> results = q.getResultList();

        if (results != null && results.size() > 0) {
            for (ParcelSearchResult parcel : results) {
                parcel.setPropCodes(searchPropCodesByParcel(langCode, parcel.getId()));
            }
        }
        return results;
    }

    /**
     * Searches parcel by its id.
     *
     * @param langCode Language code for localization
     * @param id Parcel id
     * @return
     */
    public ParcelSearchResult searchParcelById(String langCode, String id) {
        if (StringUtility.isEmpty(id)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PARCEL_SEARCH_SELECT + " where p.id = :id", ParcelSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("id", id);
        ParcelSearchResult result = (ParcelSearchResult) q.getSingleResult();

        if (result != null) {
            result.setPropCodes(searchPropCodesByParcel(langCode, result.getId()));
        }
        return result;
    }

    /**
     * Searches property codes (CCROs) by parcel id.
     *
     * @param langCode Language code for localization
     * @param parcelId Parcel id
     * @return
     */
    public List<PropertyCodeSearchResult> searchPropCodesByParcel(String langCode, String parcelId) {
        if (StringUtility.isEmpty(parcelId)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PROP_CODE_SEARCH_SELECT + " where p.parcel_id = :parcelId order by p.prop_number", PropertyCodeSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("parcelId", parcelId);
        return q.getResultList();
    }

    /**
     * Searches property codes (CCROs) by application id.
     *
     * @param langCode Language code for localization
     * @param appId Application id
     * @return
     */
    public List<PropertyCodeSearchResult> searchPropCodesByApplication(String langCode, String appId) {
        if (StringUtility.isEmpty(appId)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PROP_CODE_SEARCH_SELECT + " where p.id in (select property_id from public.application_property where app_id = :appId) order by p.prop_number", PropertyCodeSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("appId", appId);
        return q.getResultList();
    }
    
    /**
     * Searches property code by property id.
     *
     * @param langCode Language code for localization
     * @param propId Property id
     * @return
     */
    public PropertyCodeSearchResult searchPropCodeById(String langCode, String propId) {
        if (StringUtility.isEmpty(propId)) {
            return null;
        }

        // Prepare params
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PROP_CODE_SEARCH_SELECT + " where p.id = :propId", PropertyCodeSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("propId", propId);
        return (PropertyCodeSearchResult)q.getSingleResult();
    }
}

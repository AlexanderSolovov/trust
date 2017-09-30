package com.dai.trust.services.search;

import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.models.search.ApplicationSearchParams;
import com.dai.trust.models.search.ApplicationSearchResult;
import com.dai.trust.models.search.LegalEntitySearchResult;
import com.dai.trust.models.search.PersonSearchResult;
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

    private final String APP_SEARCH_ORDER = "order by a.lodgement_date desc ";

    public SearchService() {
        super();
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
                + "\n where a.complete_date is null and a.complete_date is null and a.assignee = :username"
                + " limit 1000", ApplicationSearchResult.class);
        q.setParameter("langCode", langCode);
        q.setParameter("username", SharedData.getUserName());
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
        return (ApplicationSearchResult)q.getSingleResult();
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
}

package com.dai.trust.services.search;

import com.dai.trust.common.StringUtility;
import com.dai.trust.models.search.LegalEntitySearchResult;
import com.dai.trust.models.search.PersonSearchResult;
import com.dai.trust.services.AbstractService;
import java.util.List;
import javax.persistence.Query;

/**
 * Contains various search methods.
 */
public class SearchService extends AbstractService {

    public SearchService() {
        super();
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
        if(StringUtility.isEmpty(langCode)){
            langCode = "en";
        }
        if(StringUtility.isEmpty(name)){
            name = "%";
        } else {
            name = "%" + name.toLowerCase().trim().replace(" ", "%") + "%";
        }
        if(StringUtility.isEmpty(idNumber)){
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
        if(StringUtility.isEmpty(langCode)){
            langCode = "en";
        }
        if(StringUtility.isEmpty(name)){
            name = "%";
        } else {
            name = "%" + name.toLowerCase().trim() + "%";
        }
        if(StringUtility.isEmpty(regNumber)){
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
}

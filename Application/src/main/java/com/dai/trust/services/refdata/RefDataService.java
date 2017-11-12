package com.dai.trust.services.refdata;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.AbstractRefDataEntity;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.AppTypeGroup;
import com.dai.trust.models.refdata.District;
import com.dai.trust.models.refdata.Hamlet;
import com.dai.trust.models.refdata.Language;
import com.dai.trust.models.refdata.RightType;
import com.dai.trust.models.refdata.Village;
import com.dai.trust.services.AbstractService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Table;

/**
 * Contains methods, related to managing different reference data tables.
 */
public class RefDataService extends AbstractService {

    public RefDataService() {
        super();
    }

    /** 
     * Returns right types by application type code. 
     * @param appTypeCode Application type code
     * @param langCode Language code for localization. If null or empty value is
     * provided, then unlocalized full string will be returned.
     * @return 
     */
    public List<RightType> getRightTypesByAppType(String appTypeCode, String langCode) {
        List<RightType> result = getEM().createNativeQuery(
                "SELECT " + getRefDataColumns("t", langCode) + getExtraColumns(RightType.class)
                + " FROM ref_right_type t "
                + "WHERE t.active = true and t.code in (select right_type_code from ref_app_type_right_type where app_type_code = :appTypeCode) ORDER BY t.val",
                RightType.class).setParameter("appTypeCode", appTypeCode).getResultList();
        return result;
    }

    /**
     * Returns reference data table records.
     *
     * @param <T> Reference data class type
     * @param clazz Reference data class
     * @param onlyActive Indicate if only active records have to be returned. If
     * false, then all records will be returned
     * @param langCode Language code for localization. If null or empty value is
     * provided, then unlocalized full string will be returned.
     * @return
     */
    public <T extends AbstractRefDataEntity> List<T> getRefDataRecords(Class<T> clazz, boolean onlyActive, String langCode) {
        String refColumns;
        String table = clazz.getName();
        Table t = clazz.getAnnotation(Table.class);
        String where = onlyActive ? " WHERE t.active = true" : "";

        if (t != null && !StringUtility.isEmpty(t.name())) {
            table = t.name();
        }

        table = table + " t";

        if (StringUtility.isEmpty(langCode)) {
            // Return unlocalizaed languages
            refColumns = getRefDataColumnsUnlocalized("t");
        } else {
            // Return localizaed languages
            refColumns = getRefDataColumns("t", langCode);
        }

        List<T> result = getEM().createNativeQuery(
                "SELECT " + refColumns + getExtraColumns(clazz) + getFromClause(clazz, table) + where + getOrderByColumn(clazz),
                clazz).getResultList();

        // Make recursive requests for sublists if any.
        populateSubLists(result, onlyActive, langCode);
        return result;
    }

    /**
     * Returns reference data item by code.
     *
     * @param <T> Reference data class type
     * @param clazz Reference data class
     * @param code Reference data item code
     * @param langCode Language code for localization. If null or empty value is
     * provided, then unlocalized full string will be returned.
     * @return
     */
    public <T extends AbstractRefDataEntity> T getRefDataRecord(Class<T> clazz, String code, String langCode) {
        String refColumns;
        String table = clazz.getName();
        Table t = clazz.getAnnotation(Table.class);

        if (t != null && !StringUtility.isEmpty(t.name())) {
            table = t.name();
        }

        table = table + " t";

        if (StringUtility.isEmpty(langCode)) {
            // Return unlocalizaed languages
            refColumns = getRefDataColumnsUnlocalized("t");
        } else {
            // Return localizaed languages
            refColumns = getRefDataColumns("t", langCode);
        }

        T result = (T) getEM().createNativeQuery(
                "SELECT " + refColumns + getExtraColumns(clazz) + getFromClause(clazz, table) + " WHERE t.code=:code",
                clazz)
                .setParameter("code", code)
                .getSingleResult();

        populateSubLists(result, true, langCode);
        return result;
    }

    // Populate sublist on the provided items
    private <T extends AbstractRefDataEntity> void populateSubLists(List<T> items, boolean onlyActive, String langCode) {
        if (items != null && items.size() > 0) {
            if (AppTypeGroup.class.isAssignableFrom(items.get(0).getClass())) {
                List<AppType> subList = getRefDataRecords(AppType.class, onlyActive, langCode);
                for (T item : items) {
                    List<AppType> result = new ArrayList<>();
                    if (subList != null) {
                        for (AppType appType : subList) {
                            if (appType.getAppTypeGroupCode().equals(item.getCode())) {
                                result.add(appType);
                            }
                        }
                    }
                    ((AppTypeGroup) item).setAppTypes(result);
                }
            }
        }
    }

    // Populate sublist for one item
    private <T extends AbstractRefDataEntity> void populateSubLists(T item, boolean onlyActive, String langCode) {
        if (item != null) {
            List<T> tmpList = new ArrayList<>();
            tmpList.add(item);
            populateSubLists(tmpList, onlyActive, langCode);
        }
    }

    // Returns order by column based on the reference data object
    private <T extends AbstractRefDataEntity> String getOrderByColumn(Class<T> clazz) {
        if (Language.class.isAssignableFrom(clazz)) {
            return " ORDER BY t.item_order";
        }
        return " ORDER BY t.val";
    }

    // Returns from clause
    private <T extends AbstractRefDataEntity> String getFromClause(Class<T> clazz, String table) {
        if (AppType.class.isAssignableFrom(clazz)) {
            return " FROM " + table + " JOIN ref_app_type_right_type art ON t.code = art.app_type_code";
        }
        return " FROM " + table;
    }

    // Returns extra columns of reference data object
    private <T extends AbstractRefDataEntity> String getExtraColumns(Class<T> clazz) {
        if (Language.class.isAssignableFrom(clazz)) {
            return ", t.ltr, t.item_order, t.is_default";
        } else if (RightType.class.isAssignableFrom(clazz)) {
            return ", t.right_type_group_code, t.allow_multiple";
        } else if (AppType.class.isAssignableFrom(clazz)) {
            return ", t.app_type_group_code, t.transaction_type_code, art.*";
        }
        return "";
    }

    /**
     * Saves setting to the database
     *
     * @param <T> Reference data class type
     * @param refData Reference data object to save
     * @return Returns saved setting
     */
    public <T extends AbstractRefDataEntity> T saveRefDataRecord(T refData) {
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();

        if (StringUtility.isEmpty(StringUtility.empty(refData.getCode()).replace(" ", ""))) {
            errors.addError(new TrustException(MessagesKeys.ERR_CODE_EMPTY));
        } else {
            refData.setCode(refData.getCode().trim());
        }

        if (StringUtility.isEmpty(refData.getVal())
                || StringUtility.isEmpty(refData.getVal().replace("::::", "").replace(" ", ""))) {
            errors.addError(new TrustException(MessagesKeys.ERR_VALUE_EMPTY));
        } else {
            refData.setVal(refData.getVal().trim());
        }

        // Make class specific validation        
        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        return save(refData, true);
    }

    /**
     * Returns list of hamlets by village code.
     *
     * @param villageCode Village code
     * @param langCode Language code
     * @return
     */
    public List<Hamlet> getHamletsByVillage(String villageCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", village_code, abbr, leader "
                + "FROM ref_hamlet "
                + "WHERE active = true and village_code = :villageCode "
                + "ORDER BY val", Hamlet.class)
                .setParameter("villageCode", villageCode).getResultList();
    }

    /**
     * Returns list of hamlets, which belong to the same village as provided
     * hamlet code.
     *
     * @param hamletCode Hamlet code
     * @param langCode Language code
     * @return
     */
    public List<Hamlet> getHamletsByHamlet(String hamletCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", village_code, abbr, leader "
                + "FROM ref_hamlet "
                + "WHERE active = true and village_code in (select village_code from ref_hamlet where code = :hamletCode) "
                + "ORDER BY val", Hamlet.class)
                .setParameter("hamletCode", hamletCode).getResultList();
    }

    /**
     * Returns list of villages by district code.
     *
     * @param districtCode District code
     * @param langCode Language code
     * @return
     */
    public List<Village> getVillagesByDistrict(String districtCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", district_code, address, chairman, executive_officer "
                + "FROM ref_village "
                + "WHERE active = true and district_code = :districtCode "
                + "ORDER BY val", Village.class)
                .setParameter("districtCode", districtCode).getResultList();
    }

    /**
     * Returns list of villages, which belong to the same district as provided
     * village code.
     *
     * @param villageCode Village code
     * @param langCode Language code
     * @return
     */
    public List<Village> getVillagesByVillage(String villageCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", district_code, address, chairman, executive_officer "
                + "FROM ref_village "
                + "WHERE active = true and district_code in (select district_code from ref_village where code = :villageCode) "
                + "ORDER BY val", Village.class)
                .setParameter("villageCode", villageCode).getResultList();
    }

    /**
     * Returns list of districts by region code.
     *
     * @param regionCode Region code
     * @param langCode Language code
     * @return
     */
    public List<District> getDistrictsByRegion(String regionCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", region_code "
                + "FROM ref_district "
                + "WHERE active = true and region_code = :regionCode "
                + "ORDER BY val", District.class)
                .setParameter("regionCode", regionCode).getResultList();
    }

    /**
     * Returns list of districts, which belong to the same region as provided
     * district code.
     *
     * @param districtCode District code
     * @param langCode Language code
     * @return
     */
    public List<District> getDistrictsByDistrict(String districtCode, String langCode) {
        return getEM().createNativeQuery(
                "SELECT " + getRefDataColumns(langCode) + ", region_code "
                + "FROM ref_district "
                + "WHERE active = true and region_code in (select region_code from ref_district where code = :districtCode) "
                + "ORDER BY val", District.class)
                .setParameter("districtCode", districtCode).getResultList();
    }
}

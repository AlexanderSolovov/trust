package com.dai.trust.services.refdata;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.AbstractRefDataEntity;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.AppTypeGroup;
import com.dai.trust.models.refdata.Language;
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
        String where = onlyActive ? " WHERE active = true" : "";
        String table = clazz.getName();
        Table t = clazz.getAnnotation(Table.class);

        if (t != null && !StringUtility.isEmpty(t.name())) {
            table = t.name();
        }

        if (StringUtility.isEmpty(langCode)) {
            // Return unlocalizaed languages
            refColumns = getRefDataColumnsUnlocalized(null);
        } else {
            // Return localizaed languages
            refColumns = getRefDataColumns(langCode);
        }

        List<T> result = getEM().createNativeQuery(
                "SELECT " + refColumns + getExtraColumns(clazz) + " FROM " + table + where + " ORDER BY " + getOrderByColumn(clazz),
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

        if (StringUtility.isEmpty(langCode)) {
            // Return unlocalizaed languages
            refColumns = getRefDataColumnsUnlocalized(null);
        } else {
            // Return localizaed languages
            refColumns = getRefDataColumns(langCode);
        }

        T result = (T) getEM().createNativeQuery(
                "SELECT " + refColumns + getExtraColumns(clazz) + " FROM " + table + " WHERE code=:code",
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
        if(item != null){
            List<T> tmpList = new ArrayList<>();
            tmpList.add(item);
            populateSubLists(tmpList, onlyActive, langCode);
        }
    }

    // Returns ordder by column based on the reference data object
    private <T extends AbstractRefDataEntity> String getOrderByColumn(Class<T> clazz) {
        if (Language.class.isAssignableFrom(clazz)) {
            return "item_order";
        }
        return "val";
    }

    // Returns extra columns of reference data object
    private <T extends AbstractRefDataEntity> String getExtraColumns(Class<T> clazz) {
        if (Language.class.isAssignableFrom(clazz)) {
            return ", ltr, item_order, is_default";
        } else if (AppType.class.isAssignableFrom(clazz)) {
            return ", app_type_group_code, transaction_type_code";
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
}

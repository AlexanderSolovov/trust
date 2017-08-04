package com.dai.trust.services.refdata;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.AbstractRefDataEntity;
import com.dai.trust.models.refdata.Language;
import com.dai.trust.services.AbstractService;
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
        String extraColumns = "";
        String orderBy = "val";
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

        // Set extra columns and sort order based on class type
        if (Language.class.isAssignableFrom(clazz)) {
            extraColumns = ", ltr, item_order, is_default";
            orderBy = "item_order";
        }

        return getEM().createNativeQuery(
                "SELECT " + refColumns + extraColumns + " FROM " + table + where + " ORDER BY " + orderBy,
                clazz).getResultList();
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
        String extraColumns = "";
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

        // Set extra columns based on class type
        if (Language.class.isAssignableFrom(clazz)) {
            extraColumns = ", ltr, item_order, is_default";
        }

        return (T) getEM().createNativeQuery(
                "SELECT " + refColumns + extraColumns + " FROM " + table + " WHERE code=:code",
                clazz)
                .setParameter("code", code)
                .getSingleResult();
    }

    /**
     * Saves setting to the database
     *
     * @param <T> Reference data class type
     * @param refData Reference data object to save
     * @return Returns saved setting
     */
    public <T extends AbstractRefDataEntity> T saveRefDataREcord(T refData) {
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

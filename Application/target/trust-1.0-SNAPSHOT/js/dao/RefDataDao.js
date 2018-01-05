/* 
 * Contains methods to communicate with server to manage reference data tables
 * Requires Global.js
 */

var RefDataDao = RefDataDao || {};
RefDataDao.REF_DATA_TYPES = {
    DocumentType: {type: "DocumentType", labelSingle: "ref-document-type", labelPlural: "ref-document-types"},
    Language: {type: "Language", labelSingle: "ref-language", labelPlural: "ref-languages"},
    AppType: {type: "AppType", labelSingle: "ref-app-type", labelPlural: "ref-app-types"},
    AppStatus: {type: "AppStatus", labelSingle: "ref-app-status", labelPlural: "ref-app-statuses"},
    Gender: {type: "Gender", labelSingle: "ref-gender", labelPlural: "ref-genders"},
    MaritalStatus: {type: "MaritalStatus", labelSingle: "ref-marital-status", labelPlural: "ref-marital-statuses"},
    IdType: {type: "IdType", labelSingle: "ref-id-type", labelPlural: "ref-id-types"},
    Citizenship: {type: "Citizenship", labelSingle: "ref-citizenship", labelPlural: "ref-citizenships"},
    PartyStatus: {type: "PartyStatus", labelSingle: "ref-party-status", labelPlural: "ref-party-statuses"},
    LegalEntityType: {type: "LegalEntityType", labelSingle: "ref-le-type", labelPlural: "ref-le-types"},
    Region: {type: "Region", labelSingle: "ref-region", labelPlural: "ref-regions"},
    District: {type: "District", labelSingle: "ref-district", labelPlural: "ref-districts"},
    Village: {type: "Village", labelSingle: "ref-village", labelPlural: "ref-villages"},
    Hamlet: {type: "Hamlet", labelSingle: "ref-hamlet", labelPlural: "ref-hamlets"},
    LandType: {type: "LandType", labelSingle: "ref-land-type", labelPlural: "ref-land-types"},
    RegStatus: {type: "RegStatus", labelSingle: "ref-reg-status", labelPlural: "ref-reg-statuses"},
    RightType: {type: "RightType", labelSingle: "ref-right-type", labelPlural: "ref-right-types"},
    OwnerType: {type: "OwnerType", labelSingle: "ref-owner-type", labelPlural: "ref-owner-types"},
    OccupancyType: {type: "OccupancyType", labelSingle: "ref-occupancy-type", labelPlural: "ref-occupancy-types"},
    LandUse: {type: "LandUse", labelSingle: "ref-landuse-type", labelPlural: "ref-landuse-types"}
};

$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/ref/";
    RefDataDao.URL_GET_RECORD = baseUrl + "get/{0}/{1}";
    RefDataDao.URL_GET_RECORD_UNLOCALIZED = baseUrl + "get/{0}/{1}?unlocalized";
    RefDataDao.URL_GET_ACTIVE_RECORDS = baseUrl + "getlist/{0}";
    RefDataDao.URL_GET_ACTIVE_RECORDS_UNLOCALIZED = baseUrl + "getlist/{0}?unlocalized";
    RefDataDao.URL_GET_ALL_RECORDS = baseUrl + "getall/{0}";
    RefDataDao.URL_GET_ALL_RECORDS_UNLOCALIZED = baseUrl + "getall/{0}?unlocalized";
    RefDataDao.URL_SAVE_RECORD = baseUrl + "save/{0}";
    RefDataDao.URL_DELETE_RECORD = baseUrl + "delete/{0}/{1}";
    RefDataDao.URL_GET_HAMLETS_BY_VILLAGE = baseUrl + "gethamletsbyvillage/{0}";
    RefDataDao.URL_GET_HAMLETS_BY_HAMLET = baseUrl + "gethamletsbyhamlet/{0}";
    RefDataDao.URL_GET_VILLAGES_BY_DISTRICT = baseUrl + "getvillagesbydistrict/{0}";
    RefDataDao.URL_GET_VILLAGES_BY_VILLAGE = baseUrl + "getvillagesbyvillage/{0}";
    RefDataDao.URL_GET_DISTRICTS_BY_REGION = baseUrl + "getdistrictsbyregion/{0}";
    RefDataDao.URL_GET_DISTRICTS_BY_DISTRICT = baseUrl + "getdistrictsbydistrict/{0}";
    RefDataDao.URL_GET_RIGHT_TYPES_BY_APP_TYPE_CODE = baseUrl + "getrighttypesbyapptype/{0}";
});

RefDataDao.TRANSACTION_TYPE_CODES = {
    Registration: "registration",
    FirstRegistration: "first_registration",
    Remove: "remove",
    Surrender: "surrender",
    Termination: "terminate",
    Transfer: "transfer",
    Vary: "vary",
    Rectify: "rectify",
    ChangeName: "change_name"
};

RefDataDao.RIGHT_TYPE_GROUP_CODES = {
    Ownership: "ownership",
    Restriction: "restriction"
};

RefDataDao.RIGHT_TYPE_CODES = {
    Ccro: "ccro",
    Caveat: "caveat",
    Mortgage: "mortgage"
};

RefDataDao.OWNER_TYPE_CODES = {
    Owner: "owner",
    Administrator: "administrator",
    Guardian: "guardian"
};

RefDataDao.OCCUPANCY_TYPE_CODES = {
    Common: "common",
    Guardian: "guardian",
    Joint: "joint",
    NonNatural: "nonnatural",
    Probate: "probate",
    Single: "single"
};

RefDataDao.RefData = function () {
    return {code: "", val: "", active: true, version: 0};
};

RefDataDao.Language = function () {
    return {code: "", val: "", active: true, isDefault: false, itemOrder: 0, ltr: true, version: 0};
};

RefDataDao.getActiveRecords = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert, useCache) {
    var data;
    var localSusscess = successAction;
    if (useCache) {
        var data = getFromCache("active_" + refDataType);
        if (typeof data !== 'undefined') {
            if (isFunction(successAction)) {
                successAction(data);
            }
            if (isFunction(alwaysAction)) {
                alwaysAction(data);
            }
            return;
        }
        var localSusscess = function (data) {
            saveToCache("active_" + refDataType, data);
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        };
    }

    getAjaxData(String.format(RefDataDao.URL_GET_ACTIVE_RECORDS, refDataType), localSusscess, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getActiveRecordsUnlocalized = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_ACTIVE_RECORDS_UNLOCALIZED, refDataType), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getAllRecords = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert, useCache) {
    var data;
    var localSusscess = successAction;
    if (useCache) {
        var data = getFromCache("all_" + refDataType);
        if (typeof data !== 'undefined') {
            if (isFunction(successAction)) {
                successAction(data);
            }
            if (isFunction(alwaysAction)) {
                alwaysAction(data);
            }
            return;
        }
        var localSusscess = function (data) {
            saveToCache("all_" + refDataType, data);
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        };
    }
    getAjaxData(String.format(RefDataDao.URL_GET_ALL_RECORDS, refDataType), localSusscess, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getAllRecordsUnlocalized = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_ALL_RECORDS_UNLOCALIZED, refDataType), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getRecord = function (refDataType, code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_RECORD, refDataType, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getRecordUnlocalized = function (refDataType, code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_RECORD_UNLOCALIZED, refDataType, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.saveRecord = function (refDataType, data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(String.format(RefDataDao.URL_SAVE_RECORD, refDataType), data, successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.deleteRecord = function (refDataType, id, successAction, failAction, alwaysAction, showErrorAlert) {
    deleteAjaxData(String.format(RefDataDao.URL_DELETE_RECORD, refDataType, id), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getHamletsByVillage = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_HAMLETS_BY_VILLAGE, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getHamletsByHamlet = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_HAMLETS_BY_HAMLET, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getVillagesByDistrict = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_VILLAGES_BY_DISTRICT, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getVillagesByVillage = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_VILLAGES_BY_VILLAGE, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getDistrictsByRegion = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_DISTRICTS_BY_REGION, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getDistrictsByDistrict = function (code, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_DISTRICTS_BY_DISTRICT, code), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getRightTypesByAppType = function (appTypeCode, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_RIGHT_TYPES_BY_APP_TYPE_CODE, appTypeCode), successAction, failAction, alwaysAction, showErrorAlert);
};

/**
 * Filters list of reference data records and returns only active records.
 * @param allRecords List of records to filter.
 */
RefDataDao.filterActiveRecords = function (allRecords) {
    var result = [];
    if (!isNull(allRecords)) {
        for (i = 0; i < allRecords.length; i++) {
            if (allRecords[i].active) {
                result.push(allRecords[i]);
            }
        }
    }
    return result;
};

/**
 * Searches provided list of reference data for specified code.
 * @param list List of reference data objects.
 * @param code Code to search for.
 */
RefDataDao.getRefDataByCode = function (list, code) {
    if (!isNull(list) && !isNull(code)) {
        for (i = 0; i < list.length; i++) {
            if (list[i].code === code) {
                return list[i];
            }
        }
    }
    return null;
};
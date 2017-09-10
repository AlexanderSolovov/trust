/* 
 * Contains methods to communicate with server to manage reference data tables
 */

var RefDataDao = RefDataDao || {};
RefDataDao.REF_DATA_TYPES = {
    DocumentType: {type: "DocumentType", labelSingle: "ref-document-type", labelPlural: "ref-document-types"},
    Language: {type: "Language", labelSingle: "ref-language", labelPlural: "ref-languages"},
    AppType: {type: "AppType", labelSingle: "ref-app-type", labelPlural: "ref-app-types"},
    Gender: {type: "Gender", labelSingle: "ref-gender", labelPlural: "ref-genders"},
    MaritalStatus: {type: "MaritalStatus", labelSingle: "ref-marital-status", labelPlural: "ref-marital-statuses"},
    IdType: {type: "IdType", labelSingle: "ref-id-type", labelPlural: "ref-id-types"},
    Citizenship: {type: "Citizenship", labelSingle: "ref-citizenship", labelPlural: "ref-citizenships"},
    PartyStatus: {type: "PartyStatus", labelSingle: "ref-party-status", labelPlural: "ref-party-statuses"},
    LegalEntityType: {type: "LegalEntityType", labelSingle: "ref-le-type", labelPlural: "ref-le-types"}
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
});

RefDataDao.RefData = function () {
    return {code: "", val: "", active: true, version: 1};
};

RefDataDao.Language = function () {
    return {code: "", val: "", active: true, isDefault: false, itemOrder: 0, ltr: true, version: 1};
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
        var localSusscess = function(data){
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
        var localSusscess = function(data){
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


/**
 * Filters list of reference data records and returns only active records.
 * @param allRecords List of records to filter.
 */
RefDataDao.filterActiveRecords = function (allRecords){
    var result = [];
    if(!isNull(allRecords)){
        for(i=0; i<allRecords.length; i++){
            if(allRecords[i].active){
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
RefDataDao.getRefDataByCode = function getActiveRecords(list, code){
    if(!isNull(list) && !isNull(code)){
        for(i=0; i<list.length; i++){
            if(list[i].code === code){
                return list[i];
            }
        }
    }
    return null;
};
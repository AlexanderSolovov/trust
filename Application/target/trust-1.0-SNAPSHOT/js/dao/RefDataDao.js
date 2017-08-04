/* 
 * Contains methods to communicate with server to manage reference data tables
 */

var RefDataDao = RefDataDao || {};
RefDataDao.REF_DATA_TYPES = {
    DocumentType: {type: "DocumentType", labelSingle: "ref-document-type", labelPlural: "ref-document-types"},
    Language: {type: "Language", labelSingle: "ref-language", labelPlural: "ref-languages"}
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

RefDataDao.getActiveRecords = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_ACTIVE_RECORDS, refDataType), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getActiveRecordsUnlocalized = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_ACTIVE_RECORDS_UNLOCALIZED, refDataType), successAction, failAction, alwaysAction, showErrorAlert);
};

RefDataDao.getAllRecords = function (refDataType, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(RefDataDao.URL_GET_ALL_RECORDS, refDataType), successAction, failAction, alwaysAction, showErrorAlert);
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
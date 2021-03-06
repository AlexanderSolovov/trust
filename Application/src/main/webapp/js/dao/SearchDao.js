/* 
 * Contains methods to communicate with server to manage parties
 * Requires Global.js
 */

var SearchDao = SearchDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/search/";
    SearchDao.URL_PERSON_SEARCH = baseUrl + "person?name={0}&idnumber={1}&ccro={2}";
    SearchDao.URL_LEGAL_ENTITY_SEARCH = baseUrl + "legalentity?name={0}&regnumber={1}&ccro={2}";
    SearchDao.URL_FULL_USER_NAME_SEARCH = baseUrl + "userfullname/{0}";
    SearchDao.URL_SEARCH_MY_APPS = baseUrl + "myapplications";
    SearchDao.URL_SEARCH_PENDING_APPS = baseUrl + "pendingapplications";
    SearchDao.URL_SEARCH_APP_BY_ID = baseUrl + "application/{0}";
    SearchDao.URL_SEARCH_USERS_FOR_ASSIGNMENT = baseUrl + "usersforassignment";
    SearchDao.URL_SEARCH_APPS = baseUrl + "applications";
    SearchDao.URL_SEARCH_PARCEL_BY_POINT = baseUrl + "parcelbypoint?x={0}&y={1}";
    SearchDao.URL_SEARCH_PARCELS_BY_APPLICATION = baseUrl + "parcelsbyapplication/{0}";
    SearchDao.URL_SEARCH_APPLICATION_PROPS = baseUrl + "applicationproperties/{0}";
    SearchDao.URL_SEARCH_PARCEL_BY_ID = baseUrl + "parcelbyid/{0}";
    SearchDao.URL_SEARCH_AFFECTED_OBJECTS = baseUrl + "affectedobjectsbyapplication/{0}";
    SearchDao.URL_SEARCH_RIGHTS = baseUrl + "rights";
});

SearchDao.PersonSearchResult = function () {
    return {
        id: null,
        name: null,
        address: null,
        mobileNumber: null,
        idType: null,
        idNumber: null,
        dob: null,
        statusCode: null
    };
};

SearchDao.LegalEntitySearchResult = function () {
    return {
        id: null,
        name: null,
        regNumber: null,
        entityType: null,
        establishmentDate: null,
        address: null,
        mobileNumber: null,
        statusCode: null
    };
};

SearchDao.AppSearchParams = function () {
    return {
        number: null,
        typeCode: null,
        applicantName: null,
        applicantIdNumber: null,
        statusCode: null,
        ccroNumber: null,
        lodgemenetDateFrom: null,
        lodgemenetDateTo: null
    };
};

SearchDao.RightSearchParams = function () {
    return {
        propNumber: null,
        fileNumber: null,
        ukaNumber: null,
        rightTypeCode: null,
        rightholderName: null,
        rightholderIdNumber: null,
        statusCode: null
    };
};

SearchDao.searchAffectedObjects = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_AFFECTED_OBJECTS, appId), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchMyApps = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SearchDao.URL_SEARCH_MY_APPS, successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchPendingApps = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SearchDao.URL_SEARCH_PENDING_APPS, successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchAppById = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_APP_BY_ID, id), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchApplications = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(SearchDao.URL_SEARCH_APPS, data, successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchPerson = function (name, idNumber, ccro, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_PERSON_SEARCH, String.empty(name), String.empty(idNumber), String.empty(ccro)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchLegalEntity = function (name, regNumber, ccro, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_LEGAL_ENTITY_SEARCH, String.empty(name), String.empty(regNumber), String.empty(ccro)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchFullUserName = function (userName, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_FULL_USER_NAME_SEARCH, String.empty(userName)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchUsersForAssignment = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SearchDao.URL_SEARCH_USERS_FOR_ASSIGNMENT, successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchParcelByPoint = function (x, y, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_PARCEL_BY_POINT, x, y), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchParcelsByApp = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_PARCELS_BY_APPLICATION, appId), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchApplicationProps = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_APPLICATION_PROPS, appId), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchParcelById = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_SEARCH_PARCEL_BY_ID, id), successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchRights = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(SearchDao.URL_SEARCH_RIGHTS, data, successAction, failAction, alwaysAction, showErrorAlert);
};
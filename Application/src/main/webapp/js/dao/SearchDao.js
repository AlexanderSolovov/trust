/* 
 * Contains methods to communicate with server to manage parties
 * Requires Global.js
 */

var SearchDao = SearchDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/search/";
    SearchDao.URL_PERSON_SEARCH = baseUrl + "person?name={0}&idnumber={1}";
    SearchDao.URL_LEGAL_ENTITY_SEARCH = baseUrl + "legalentity?name={0}&regnumber={1}";
    SearchDao.URL_FULL_USER_NAME_SEARCH = baseUrl + "userfullname/{0}";
    SearchDao.URL_SEARCH_MY_APPS = baseUrl + "myapplications";
    SearchDao.URL_SEARCH_PENDING_APPS = baseUrl + "pendingapplications";
    SearchDao.URL_SEARCH_APP_BY_ID = baseUrl + "application/{0}";
    SearchDao.URL_SEARCH_USERS_FOR_ASSIGNMENT = baseUrl + "usersforassignment";
    SearchDao.URL_SEARCH_APPS = baseUrl + "applications";
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

SearchDao.searchPerson = function (name, idNumber, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_PERSON_SEARCH, String.empty(name), String.empty(idNumber)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchLegalEntity = function (name, regNumber, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_LEGAL_ENTITY_SEARCH, String.empty(name), String.empty(regNumber)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchFullUserName = function (userName, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_FULL_USER_NAME_SEARCH, String.empty(userName)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchUsersForAssignment = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SearchDao.URL_SEARCH_USERS_FOR_ASSIGNMENT, successAction, failAction, alwaysAction, showErrorAlert);
};
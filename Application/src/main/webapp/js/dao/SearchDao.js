/* 
 * Contains methods to communicate with server to manage parties
 */

var SearchDao = SearchDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/search/";
    SearchDao.URL_PERSON_SEARCH = baseUrl + "person?name={0}&idnumber={1}";
    SearchDao.URL_LEGAL_ENTITY_SEARCH = baseUrl + "legalentity?name={0}&regnumber={1}";
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

SearchDao.searchPerson = function (name, idNumber, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_PERSON_SEARCH, String.empty(name), String.empty(idNumber)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

SearchDao.searchLegalEntity = function (name, regNumber, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(SearchDao.URL_LEGAL_ENTITY_SEARCH, String.empty(name), String.empty(regNumber)),
            successAction, failAction, alwaysAction, showErrorAlert);
};
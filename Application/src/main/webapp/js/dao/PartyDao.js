/* 
 * Contains methods to communicate with server to manage parties
 */

var PartyDao = PartyDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/party/";
    PartyDao.URL_GET_PERSON = baseUrl + "getperson/{0}";
    PartyDao.URL_GET_LEGAL_ENTITY = baseUrl + "getlegalentity/{0}";
});

PartyDao.Person = function () {
    return {
        id: null,
        firstName: null,
        lastName: null,
        middleName: null,
        alias: null,
        fullName: null,
        citizenshipCode: null,
        genderCode: null,
        idTypeCode: null,
        idNumber: null,
        maritalStatusCode: null,
        dob: null,
        isResident: true,
        personPhotoId: null,
        documents: null,
        address: null,
        mobileNumber: null,
        parentId: null,
        applicationId: null,
        endApplicationId: null,
        statusCode: null,
        editable: true,
        version: 1
    };
};

PartyDao.LegalEntity = function () {
    return {
        id: null,
        name: null,
        regNumber: null,
        entityTypeCode: null,
        establishmentDate: null,
        documents: null,
        address: null,
        mobileNumber: null,
        parentId: null,
        applicationId: null,
        endApplicationId: null,
        statusCode: null,
        editable: true,
        version: 1
    };
};

PartyDao.getPerson = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PartyDao.URL_GET_PERSON, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PartyDao.getLegalEntity = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PartyDao.URL_GET_LEGAL_ENTITY, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};
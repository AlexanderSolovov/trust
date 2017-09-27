/* 
 * Contains methods to communicate with server to manage parties
 * Requires Global.js
 */

var PartyDao = PartyDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/party/";
    PartyDao.URL_GET_PARTY = baseUrl + "getparty/{0}";
});

PartyDao.Party = function () {
    return {
        id: null,
        name1: null,
        name2: null,
        name3: null,
        name4: null,
        fullName: null,
        citizenshipCode: null,
        genderCode: null,
        idTypeCode: null,
        idNumber: null,
        maritalStatusCode: null,
        dob: null,
        isResident: true,
        isPrivate: true,
        entityTypeCode: null,
        personPhotoId: null,
        documents: null,
        address: null,
        mobileNumber: null,
        parentId: null,
        applicationId: null,
        endApplicationId: null,
        statusCode: null,
        editable: true,
        version: 0
    };
};

PartyDao.getParty = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PartyDao.URL_GET_PARTY, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};
/* 
 * Contains methods to communicate with server to manage property objects and parcels
 * Requires Global.js
 */

var PropertyDao = PropertyDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/property/";
    PropertyDao.URL_GET_PROPERTY = baseUrl + "getproperty/{0}";
    PropertyDao.URL_GET_PROPERTIES_BY_APP = baseUrl + "getpropertiessbyapplication/{0}";
    PropertyDao.URL_GET_PARCEL = baseUrl + "getparcel/{0}";
    PropertyDao.URL_GET_PARCELS_BY_APP = baseUrl + "getparcelsbyapplication/{0}";
    PropertyDao.URL_GET_CREATE_PARCELS_BY_APP = baseUrl + "getcreateparcelsbyapplication/{0}";
    PropertyDao.URL_SAVE_PARCELS = baseUrl + "saveparcels";
    PropertyDao.URL_SAVE_PROPERTY = baseUrl + "saveproperty";
    PropertyDao.URL_GET_PROPERTY_BY_RIGHT = baseUrl + "getpropertybyright/{0}";
});

PropertyDao.Parcel = function () {
    return {
        id: null,
        landTypeCode: null,
        uka: null,
        surveyDate: null,
        hamletCode: null,
        address: null,
        geom: null,
        comment: null,
        applicationId: null,
        endApplicationId: null,
        statusCode: null,
        version: 0
    };
};

PropertyDao.Property = function () {
    return {
        id: null,
        parcelId: null,
        fileNumber: null,
        regDate: null,
        propNumber: null,
        terminationDate: null,
        applicationId: null,
        endApplicationId: null,
        statusCode: null,
        rights: null,
        version: 0
    };
};

PropertyDao.Right = function () {
    return {
        id: null,
        parentId: null,
        rightTypeCode: null,
        duration: null,
        folioNumber: null,
        regDate: null,
        startDate: null,
        endDate: null,
        occupancyTypeCode: null,
        annualFee: null,
        juridicalArea: null,
        dealAmount: null,
        declaredLanduseCode: null,
        approvedLanduseCode: null,
        neighborNorth: null,
        neighborSouth: null,
        neighborEast: null,
        neighborWest: null,
        adjudicator1: null,
        adjudicator2: null,
        witness1: null,
        witness2: null,
        witness3: null,
        allocationDate: null,
        interesetRate: null,
        rightholders: null,
        documents: null,
        pois: null,
        deceasedOwner: null,
        description: null,
        applicationId: null,
        endApplicationId: null,
        terminationApplicationId: null,
        terminationDate: null,
        statusCode: null,
        version: 0
    };
};

PropertyDao.Poi = function () {
    return {
        id: null,
        firstName: null,
        lastName: null,
        middleName: null,
        dob: null,
        description: null,
        version: 0
    };
};

PropertyDao.DeceasedPerson = function () {
    return {
        id: null,
        firstName: null,
        lastName: null,
        middleName: null,
        description: null,
        version: 0
    };
};

PropertyDao.getParcel = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_PARCEL, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.getParcelsByApplication = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_PARCELS_BY_APP, String.empty(appId)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.getCreateParcelsByApplication = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_CREATE_PARCELS_BY_APP, String.empty(appId)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.getProperty = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_PROPERTY, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.getPropertyByRight = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_PROPERTY_BY_RIGHT, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.getPropertiesByApplication = function (appId, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(PropertyDao.URL_GET_PROPERTIES_BY_APP, String.empty(appId)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.saveParcels = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(PropertyDao.URL_SAVE_PARCELS, data, successAction, failAction, alwaysAction, showErrorAlert);
};

PropertyDao.saveProperty = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(PropertyDao.URL_SAVE_PROPERTY, data, successAction, failAction, alwaysAction, showErrorAlert);
};
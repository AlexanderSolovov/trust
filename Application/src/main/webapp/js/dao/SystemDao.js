/* 
 * Contains methods to communicate with server to manage different system objects (settings, users, groups, etc)
 * Requires Global.js
 */

var SystemDao = SystemDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/sys/";
    SystemDao.URL_GET_SETTINGS = baseUrl + "getsettings";
    SystemDao.URL_GET_SETTING = baseUrl + "getsetting/{0}";
    SystemDao.URL_SAVE_SETTING = baseUrl + "savesetting";
    SystemDao.URL_GET_GROUPS = baseUrl + "getgroups";
    SystemDao.URL_SAVE_GROUP = baseUrl + "savegroup";
    SystemDao.URL_DELETE_GROUP = baseUrl + "deletegroup/{0}";
    SystemDao.URL_GET_ROLES = baseUrl + "getroles";
    SystemDao.URL_GET_USERS = baseUrl + "getusers";
    SystemDao.URL_SAVE_USER = baseUrl + "saveuser";
    SystemDao.URL_DELETE_USER = baseUrl + "deleteuser/{0}";
    SystemDao.URL_GET_ACTIVE_MAP_LAYERS = baseUrl + "getactivemaplayers";
    SystemDao.URL_GET_ALL_MAP_LAYERS = baseUrl + "getallmaplayers";
    SystemDao.URL_GET_MAP_SETTINGS = baseUrl + "getmapsettings";
});

SystemDao.Setting = function () {
    return {id: null, val: null, active: true, readOnly: false, description: null, version: 0};
};

SystemDao.Group = function () {
    return {id: null, groupName: null, description: null, roleCodes: [], version: 0};
};

SystemDao.User = function () {
    return {id: null, userName: null, passwd: null, firstName: null,
        lastName: null, email: null, mobileNumber: null, active: true,
        description: null, groupCodes: [], fullName: null, version: 0};
};

SystemDao.getSettings = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_SETTINGS, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.saveSetting = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(SystemDao.URL_SAVE_SETTING, data, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getGroups = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_GROUPS, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.saveGroup = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(SystemDao.URL_SAVE_GROUP, data, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.deleteGroup = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    deleteAjaxData(String.format(SystemDao.URL_DELETE_GROUP, id), successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getRoles = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_ROLES, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getUsers = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_USERS, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.saveUser = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(SystemDao.URL_SAVE_USER, data, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.deleteUser = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    deleteAjaxData(String.format(SystemDao.URL_DELETE_USER, id), successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getActiveMapLayers = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_ACTIVE_MAP_LAYERS, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getAllMapLayers = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_ALL_MAP_LAYERS, successAction, failAction, alwaysAction, showErrorAlert);
};

SystemDao.getMapSettings = function (successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(SystemDao.URL_GET_MAP_SETTINGS, successAction, failAction, alwaysAction, showErrorAlert);
};
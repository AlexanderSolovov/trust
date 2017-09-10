/* 
 * Contains methods to communicate with server to manage applications
 */

var ApplicationDao = ApplicationDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/app/";
    ApplicationDao.URL_GET_FILE = baseUrl + "getfile/{0}";
    ApplicationDao.URL_UPLOAD_FILE = baseUrl + "upload";
});

ApplicationDao.Application = function () {
    return {id: null, appTypeCode: null, appNumber: null, version: 1};
};

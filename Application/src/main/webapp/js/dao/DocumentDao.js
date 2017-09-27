/* 
 * Contains methods to communicate with server to manage document
 * Requires Global.js
 */

var DocumentDao = DocumentDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/doc/";
    DocumentDao.URL_GET_FILE = baseUrl + "getfile/{0}";
    DocumentDao.URL_UPLOAD_FILE = baseUrl + "upload";
});

DocumentDao.Document = function () {
    return {id: null, typeCode: null, refNumber: null, docDate: null, authority: null, expiryDate: null, fileId: null, description: null, version: 0};
};

DocumentDao.uploadFile = function (file, successAction, failAction, alwaysAction, showErrorAlert) {
    var formData = new FormData();
    formData.append("file", file);

    $.ajax({
        url: DocumentDao.URL_UPLOAD_FILE,
        type: "POST",
        contentType: false,
        processData: false,
        data: formData,
        cache: false
    }).done(function (data) {
        runSafe(function () {
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        });
    }).fail(function (request, status, error) {
        runSafe(function () {
            handleAjaxError(request, status, showErrorAlert, $.i18n("err-failed-save-data"));
            if (failAction !== null && typeof failAction === "function") {
                failAction(request, status);
            }
        });
    }).always(function (response, status, error) {
        runSafe(function () {
            if (alwaysAction !== null && typeof alwaysAction === "function") {
                alwaysAction(response, status, error);
            }
        });
    });
};
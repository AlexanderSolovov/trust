/* 
 * Contains methods to communicate with server to manage applications.
 * Requires Global.js
 */

var ApplicationDao = ApplicationDao || {};
$(function () {
    var baseUrl = Global.APP_ROOT + "/ws/" + Global.LANG + "/application/";
    ApplicationDao.URL_GET_APPLICATION = baseUrl + "getapplication/{0}";
    ApplicationDao.URL_SAVE_APPLICATION = baseUrl + "saveapplication";
    ApplicationDao.URL_ASSIGN_APPLICATION = baseUrl + "assignapplication/{0}/{1}";
    ApplicationDao.URL_ASSIGN_APPLICATIONS = baseUrl + "assignapplications/{0}";
    ApplicationDao.URL_EDIT_APPLICATION = Global.APP_ROOT + "/application/application.jsp?id={0}";
    ApplicationDao.URL_VIEW_APPLICATION = Global.APP_ROOT + "/application/application.jsp?id={0}&view";
    ApplicationDao.URL_VIEW_APPLICATION_WITH_MESSAGE = ApplicationDao.URL_VIEW_APPLICATION + "&msg={1}";
});

ApplicationDao.Application = function () {
    return {
        id: null, 
        appTypeCode: null, 
        appNumber: null, 
        lodgementDate: new Date(), 
        approveRejectDate: null,
        withdrawDate: null,
        statusCode: Global.STATUS.pending,
        rejectReason: null,
        withdrawReason: null,
        assignee: null,
        assignedOn: null,
        completeDate: null,
        comment: null,
        documents: null,
        personApplicants: null,
        legalEntityApplicants: null,
        properties: null,
        permissions: {canEdit: true, canApprove: false, canWithdraw: false, canReject: false, canAssign: false, canReAssign: false, canTriggerAction: false},
        version: 0
    };
};

ApplicationDao.getApplication = function (id, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(ApplicationDao.URL_GET_APPLICATION, String.empty(id)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

ApplicationDao.saveApplication = function (data, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(ApplicationDao.URL_SAVE_APPLICATION, data, successAction, failAction, alwaysAction, showErrorAlert);
};

ApplicationDao.assignApplication = function (id, userName, successAction, failAction, alwaysAction, showErrorAlert) {
    getAjaxData(String.format(ApplicationDao.URL_ASSIGN_APPLICATION, String.empty(id), String.empty(userName)),
            successAction, failAction, alwaysAction, showErrorAlert);
};

ApplicationDao.assignApplications = function (ids, userName, successAction, failAction, alwaysAction, showErrorAlert) {
    postAjaxData(String.format(ApplicationDao.URL_ASSIGN_APPLICATIONS, String.empty(userName)), ids, successAction, failAction, alwaysAction, showErrorAlert);
};
/**
 * Contains methods to manage application page. 
 * Requires Documents.js, DocumentDao.js, PartyDao.js, SearchDao.js, Personsjs, 
 * Person.js, PersonView.js, PersonSearch.js, LegalEntities.js, LegalEntity.js, 
 * LegalEntityView.js, LegalEntitySearch.js, RefDataDao.js, ApplicationAssign.js
 * ApplicationDao.js, MapDao.js, Global.js, URLS.js
 */
var ApplicationCtrl = ApplicationCtrl || {};
ApplicationCtrl.Application = new ApplicationDao.Application();
ApplicationCtrl.AppType = null;
ApplicationCtrl.AppDocs = null;
ApplicationCtrl.LegalEntities = null;
ApplicationCtrl.Persons = null;
ApplicationCtrl.AsignControl = null;
ApplicationCtrl.view = false;
ApplicationCtrl.MESSAGES = {
    saved: "saved",
    assigned: "assigned"
};

// Load application information and show it
$(document).ready(function () {
    var app = ApplicationCtrl.Application;
    app.id = getUrlParam("id");
    app.appTypeCode = getUrlParam("type");
    ApplicationCtrl.view = !isNull(getUrlParam("view"));

    // Show notification if any
    if (!isNull(getUrlParam("msg"))) {
        var messageCode = getUrlParam("msg");
        if (messageCode === ApplicationCtrl.MESSAGES.saved) {
            showNotification($.i18n("app-saved"));
        } else if (messageCode === ApplicationCtrl.MESSAGES.assigned) {
            showNotification($.i18n("app-assigned"));
        }
    }

    if (!isNull(app.id)) {
        // Load application
        ApplicationDao.getApplication(app.id, function (data) {
            ApplicationCtrl.Application = data;
            ApplicationCtrl.postLoad(data);
        });
    } else {
        ApplicationCtrl.postLoad(app);
    }
});

ApplicationCtrl.postLoad = function (app) {
    if (isNull(app.appTypeCode)) {
        // Full stop
        showErrorMessage($.i18n("err-app-type-not-found"));
        return;
    }

    // Check if application is editable
    if (!ApplicationCtrl.view && (isNull(app.permissions) || !app.permissions.canEdit)) {
        showErrorMessage($.i18n("err-app-cant-edit"));
        return false;
    }

    var appStatus = null;
    var fullUserName = Global.USER_FULL_NAME;
    var loadingProcesses = 0;

    var showApp = function () {
        if (loadingProcesses > 0) {
            return;
        }

        $("#appStatus").text(appStatus.val);
        ApplicationCtrl.setTile(ApplicationCtrl.AppType.val, app.appNumber);

        // User name and date
        $("#lblLodgementDate").text(dateFormat(app.lodgementDate, dateFormat.masks.dateTime));
        $("#lblAssignee").text(fullUserName);

        if (isNullOrEmpty(app.assignedOn)) {
            $("#lblAssignmentDate").text(dateFormat(new Date(), dateFormat.masks.dateTime));
        } else {
            $("#lblAssignmentDate").text(dateFormat(app.assignedOn, dateFormat.masks.dateTime));
        }

        // Load CCRO numbers
        if (ApplicationCtrl.AppType.transactionTypeCode.toLowerCase() === RefDataDao.TRANSACTION_TYPE_CODES.OwnershipRegistration.toLowerCase()) {
            // Hide CCRO pane
            $("#pnlCcros").hide();
        } else {
            // Show CCRO pane
            $("#pnlCcros").show();
        }

        // Comments
        if (ApplicationCtrl.view) {
            $("#lblComments").show();
            $("#lblComments").text(String.empty(app.comment));
        } else {
            $("#txtComments").show();
            $("#txtComments").val(String.empty(app.comment));
        }

        // Rejection reason
        if (!isNullOrEmpty(app.rejectReason)) {
            $("#pnlRejectionReason").show();
            $("#lblRejectionReason").text(app.rejectReason);
        }

        // Withdrawal reason
        if (!isNullOrEmpty(app.withdrawReason)) {
            $("#pnlWithdrawalReason").show();
            $("#lblWithdrawalReason").text(app.withdrawReason);
        }

        // Customize toolbar
        $("#btnSave").hide();
        $("#btnBack").hide();
        $("#btnEdit").hide();
        $("#btnAssign").hide();
        $("#btnDrawParcel").hide();
        $("#btnManageRights").hide();
        
        if (ApplicationCtrl.view) {
            if (app.permissions.canAssign || app.permissions.canReAssign) {
                $("#btnAssign").show();
                $("#btnAssign").on("click", ApplicationCtrl.assign);
            }
            if (app.permissions.canEdit) {
                $("#btnEdit").show();
                $("#btnEdit").on("click", ApplicationCtrl.edit);
            }
            if(app.permissions.canDrawParcel){
                $("#btnDrawParcel").show();
                $("#btnDrawParcel").on("click", ApplicationCtrl.openMap);
            }
            if(app.permissions.canRegisterRight){
                $("#btnManageRights").show();
            }
        } else {
            // Hide for new applications
            if (app.version > 0 && !isNullOrEmpty(app.id)) {
                $("#btnBack").show();
                $("#btnBack").on("click", ApplicationCtrl.showView);
            }
            $("#btnSave").show();
            $("#btnSave").on("click", ApplicationCtrl.save);
        }

        // Localize
        $("#applicationDiv").i18n();

        // Show panel
        $("#applicationDiv").show();
    };

    // Get app type
    loadingProcesses += 1;
    RefDataDao.getRecord(RefDataDao.REF_DATA_TYPES.AppType.type, app.appTypeCode, function (refData) {
        ApplicationCtrl.AppType = refData;
        loadingProcesses -= 1;
        showApp();
    }, function () {
        showErrorMessage($.i18n("err-app-type-not-found"));
        return;
    });

    // Get app status
    loadingProcesses += 1;
    RefDataDao.getRecord(RefDataDao.REF_DATA_TYPES.AppStatus.type, app.statusCode, function (status) {
        appStatus = status;
        loadingProcesses -= 1;
        showApp();
    }, function () {
        showErrorMessage($.i18n("err-app-status-not-found"));
        return;
    });

    // Get asignee full name
    if (!isNullOrEmpty(app.assignee)) {
        loadingProcesses += 1;
        SearchDao.searchFullUserName(app.assignee, function (fullName) {
            fullUserName = fullName;
            loadingProcesses -= 1;
            showApp();
        });
    }

    // Load application documents
    loadingProcesses += 1;
    ApplicationCtrl.AppDocs = new Controls.Documents("appDocs", "divAppDocs", {editable: !ApplicationCtrl.view, documents: makeObjectsList(app.documents, "document")});
    ApplicationCtrl.AppDocs.init(function () {
        loadingProcesses -= 1;
        showApp();
    });

    // Load Legal entities
    loadingProcesses += 1;
    ApplicationCtrl.LegalEntities = new Controls.LegalEntities("leApplicants", "pnlLegalEntities", {legalEntities: makeObjectsList(app.applicants, "party"), editable: !ApplicationCtrl.view});
    ApplicationCtrl.LegalEntities.init(function () {
        loadingProcesses -= 1;
        showApp();
    });

    // Load persons
    loadingProcesses += 1;
    ApplicationCtrl.Persons = new Controls.Persons("personApplicants", "pnlPersons", {persons: makeObjectsList(app.applicants, "party"), editable: !ApplicationCtrl.view});
    ApplicationCtrl.Persons.init(function () {
        loadingProcesses -= 1;
        showApp();
    });
};

ApplicationCtrl.setTile = function (appType, appNumber) {
    if (isNull(appNumber)) {
        appNumber = "#" + $.i18n("gen-new");
    } else {
        appNumber = "#" + appNumber;
    }
    document.title = document.title + " " + $.i18n("app-application") + " " + appNumber + " (" + appType + ")";
    $("#appTypeName").text("(" + appType + ")");
    $("#appNumber").text(appNumber);
};

ApplicationCtrl.save = function () {
    if (!ApplicationCtrl.validate()) {
        return;
    }
    // Assemble the application and save
    var app = ApplicationCtrl.Application;
    app.comment = $("#txtComments").val();
    app.documents = makeVersionedList(app.documents, ApplicationCtrl.AppDocs.getDocuments(), "document");
    app.applicants = makeVersionedList(app.applicants, ApplicationCtrl.Persons.getPersons(), "party");
    var legalEntities = makeVersionedList(app.applicants, ApplicationCtrl.LegalEntities.getLegalEntities(), "party");

    if (!isNull(app.applicants)) {
        if (!isNull(legalEntities)) {
            app.applicants = app.applicants.concat(legalEntities);
        }
    } else {
        app.applicants = legalEntities;
    }

    ApplicationDao.saveApplication(app, function (appId) {
        // Redirect
        window.location.replace(
                String.format(URLS.VIEW_APPLICATION_WITH_MESSAGE,
                        appId.id, ApplicationCtrl.MESSAGES.saved));
    });
};

ApplicationCtrl.showView = function () {
    window.location.replace(String.format(URLS.VIEW_APPLICATION, ApplicationCtrl.Application.id));
};

ApplicationCtrl.edit = function () {
    window.location.replace(String.format(URLS.EDIT_APPLICATION, ApplicationCtrl.Application.id));
};

ApplicationCtrl.openMap = function () {
    window.location.replace(String.format(URLS.EDIT_MAP, ApplicationCtrl.Application.id));
};

ApplicationCtrl.assign = function () {
    if (ApplicationCtrl.AsignControl === null) {
        ApplicationCtrl.AsignControl = new Controls.ApplicationAssign("ctrlAssign", 'pnlAssign',
                {
                    onAssign: function () {
                        window.location.replace(String.format(
                                URLS.VIEW_APPLICATION_WITH_MESSAGE,
                                ApplicationCtrl.Application.id,
                                ApplicationCtrl.MESSAGES.assigned));
                    }
                });
        ApplicationCtrl.AsignControl.init();
    }
    
    if (!isNull(ApplicationCtrl.Application.id)) {
        ApplicationCtrl.AsignControl.showAssignDialog([ApplicationCtrl.Application.id]);
    }
};

ApplicationCtrl.validate = function () {
    var errors = [];
    // Check CCRO attached for transactions other than new registration
    if (ApplicationCtrl.AppType.transactionTypeCode.toLowerCase() !== RefDataDao.TRANSACTION_TYPE_CODES.OwnershipRegistration.toLowerCase()
            && (isNull(ApplicationCtrl.Application.properties) || ApplicationCtrl.Application.properties.length < 1)) {
        errors.push($.i18n("err-app-no-ccro"));
    }

    // Check applicants
    var persons = ApplicationCtrl.Persons.getPersons();
    var legalEntities = ApplicationCtrl.LegalEntities.getLegalEntities();

    if (!isNull(legalEntities) && legalEntities.length > 0) {
        if (legalEntities.length > 1) {
            errors.push($.i18n("err-app-one-le"));
        }
        if (isNull(persons) || persons.length < 1) {
            errors.push($.i18n("err-app-no-representative"));
        } else if (persons.length > 1) {
            errors.push($.i18n("err-app-one-representative"));
        }
    } else {
        if (isNull(persons) || persons.length < 1) {
            errors.push($.i18n("err-app-no-persons"));
        }
    }

    if (errors.length > 0) {
        alertErrorMessages(errors);
        return false;
    }
    return true;
};
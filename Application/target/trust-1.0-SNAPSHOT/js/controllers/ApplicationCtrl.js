/**
 * Contains methods to manage application page. 
 * Requires Documents.js, DocumentDao.js, PartyDao.js, SearchDao.js, Personsjs, 
 * Person.js, PersonView.js, PersonSearch.js, LegalEntities.js, LegalEntity.js, 
 * LegalEntityView.js, LegalEntitySearch.js, RefDataDao.js, ApplicationAssign.js
 * ApplicationDao.js, MapDao.js, Global.js, URLS.js, RightSearch.js
 */
var ApplicationCtrl = ApplicationCtrl || {};
ApplicationCtrl.Application = new ApplicationDao.Application();
ApplicationCtrl.AppType = null;
ApplicationCtrl.AppDocs = null;
ApplicationCtrl.LegalEntities = null;
ApplicationCtrl.Persons = null;
ApplicationCtrl.AsignControl = null;
ApplicationCtrl.view = false;
ApplicationCtrl.rightSearchControl = null;
ApplicationCtrl.MESSAGES = {
    saved: "saved",
    assigned: "assigned",
    approved: "approved",
    completed: "completed",
    rejected: "rejected",
    withdrawn: "withdrawn"
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
        } else if (messageCode === ApplicationCtrl.MESSAGES.approved) {
            showNotification($.i18n("app-approved"));
        } else if (messageCode === ApplicationCtrl.MESSAGES.completed) {
            showNotification($.i18n("app-completed"));
        } else if (messageCode === ApplicationCtrl.MESSAGES.rejected) {
            showNotification($.i18n("app-rejected"));
        } else if (messageCode === ApplicationCtrl.MESSAGES.withdrawn) {
            showNotification($.i18n("app-withdrawn"));
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
        $("#lblStatusDate").text("");
        if (isNullOrEmpty(app.withdrawDate)) {
            $("#lblStatusDate").text(dateFormat(app.withdrawDate, dateFormat.masks.dateTime));
        } else if (isNullOrEmpty(app.approveRejectDate)) {
            $("#lblStatusDate").text(dateFormat(app.approveRejectDate, dateFormat.masks.dateTime));
        }

        ApplicationCtrl.setTile(ApplicationCtrl.AppType.val, app.appNumber);

        // User name and date
        $("#lblLodgementDate").text(dateFormat(app.lodgementDate, dateFormat.masks.dateTime));
        $("#lblAssignee").text(fullUserName);
        $("#lblCompletionDate").text("");

        if (isNullOrEmpty(app.assignedOn)) {
            if (isNullOrEmpty(app.statusCode) || app.statusCode === Global.STATUS.pending) {
                $("#lblAssignmentDate").text(dateFormat(new Date(), dateFormat.masks.dateTime));
            } else {
                $("#lblAssignmentDate").text("");
            }
        } else {
            $("#lblAssignmentDate").text(dateFormat(app.assignedOn, dateFormat.masks.dateTime));
        }

        if (!isNullOrEmpty(app.completeDate)) {
            $("#lblCompletionDate").text(dateFormat(app.completeDate, dateFormat.masks.dateTime));
        }

        // Load CCRO numbers
        if (ApplicationCtrl.AppType.transactionTypeCode.toLowerCase() === RefDataDao.TRANSACTION_TYPE_CODES.FirstRegistration.toLowerCase()) {
            // Hide CCRO pane
            $("#pnlCcros").hide();
        } else {
            // Show CCRO pane
            $("#pnlCcros").show();
        }

        ApplicationCtrl.fillProperties();

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
        $("#btnApprove").hide();
        $("#btnComplete").hide();
        $("#btnReject").hide();
        $("#btnWithdraw").hide();
        $("#btnAssign").hide();
        $("#btnDrawParcel").hide();
        $("#btnManageRights").hide();

        if (ApplicationCtrl.view) {
            if (app.permissions.canApprove) {
                $("#btnApprove").show();
                $("#btnApprove").on("click", ApplicationCtrl.approve);
            }
            if (app.permissions.canComplete) {
                $("#btnComplete").show();
                $("#btnComplete").on("click", ApplicationCtrl.complete);
            }
            if (app.permissions.canReject) {
                $("#btnReject").show();
                $("#btnReject").on("click", {reject: true}, ApplicationCtrl.showReasonDialog);
            }
            if (app.permissions.canWithdraw) {
                $("#btnWithdraw").show();
                $("#btnWithdraw").on("click", {reject: false}, ApplicationCtrl.showReasonDialog);
            }
            if (app.permissions.canAssign || app.permissions.canReAssign) {
                $("#btnAssign").show();
                $("#btnAssign").on("click", ApplicationCtrl.assign);
            }
            if (app.permissions.canEdit) {
                $("#btnEdit").show();
                $("#btnEdit").on("click", ApplicationCtrl.edit);
            }
            if (app.permissions.canDrawParcel) {
                $("#btnDrawParcel").show();
                $("#btnDrawParcel").on("click", ApplicationCtrl.openMap);
            }
            if (app.permissions.canRegisterRight) {
                $("#btnManageRights").show();
                $("#btnManageRights").on("click", ApplicationCtrl.openProperty);
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

    // Get props attached to the application
    if (!isNull(ApplicationCtrl.Application) && !isNull(ApplicationCtrl.Application.properties)) {
        loadingProcesses += 1;
        SearchDao.searchApplicationProps(app.id, function (props) {
            if (!isNullOrEmpty(props) && props.length > 0) {
                $.each(props, function (i, prop) {
                    // Extend application properties with prop number
                    for (var i = 0; i < ApplicationCtrl.Application.properties.length; i++) {
                        if (ApplicationCtrl.Application.properties[i].propId === prop.id) {
                            ApplicationCtrl.Application.properties[i].propNumber = prop.propNumber;
                            break;
                        }
                    }
                });
            }
            loadingProcesses -= 1;
            showApp();
        });
    }

    // Get affected objects
    loadingProcesses += 1;
    SearchDao.searchAffectedObjects(app.id, function (result) {
        if (!isNullOrEmpty(result) && result.length > 0) {
            var parcels = [];
            var props = [];
            for (var i = 0; i < result.length; i++) {
                if (result[i].objectType === "parcel") {
                    parcels.push(result[i]);
                }
                if (result[i].objectType === "prop") {
                    props.push(result[i]);
                }
            }

            if (parcels.length > 0) {
                $("#pnlAffectedParcels").show();
                $.each(parcels, function (i, item) {
                    $("#listAffectedParcels")
                            .append($("<li />")
                                    .html(String.format(
                                            DataTablesUtility.getViewLinkCurrentWindow(),
                                            String.format(URLS.VIEW_MAP_WITH_PARCEL, item.id),
                                            item.label) + " (" + item.action + ")"
                                            ));
                });
            }

            if (props.length > 0) {
                $("#pnlAffectedProperties").show();
                $.each(props, function (i, item) {
                    $("#listAffectedProperties")
                            .append($("<li />")
                                    .html(String.format(
                                            DataTablesUtility.getViewLinkCurrentWindow(),
                                            String.format(URLS.VIEW_PROPERTY, item.id),
                                            item.label) + " (" + item.action + ")"
                                            ));
                });
            }
        }
        loadingProcesses -= 1;
        showApp();
    });

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
    } else if (!isNullOrEmpty(app.statusCode) && app.statusCode !== Global.STATUS.pending) {
        fullUserName = "";
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

ApplicationCtrl.fillProperties = function () {
    $("#spanCcros").html("");

    if (!isNull(ApplicationCtrl.Application) && !isNull(ApplicationCtrl.Application.properties)) {
        var ccroLink = DataTablesUtility.getViewLinkNewWindow();
        if (ApplicationCtrl.view) {
            ccroLink = DataTablesUtility.getViewLinkCurrentWindow();
        }

        for (var i = 0; i < ApplicationCtrl.Application.properties.length; i++) {
            var deleteButton = "";
            var separator = "";

            if (!ApplicationCtrl.view) {
                deleteButton = String.format(DataTablesUtility.getDeleteLink(), "ApplicationCtrl.deleteProp('" + ApplicationCtrl.Application.properties[i].propId + "')") + " ";
            }

            if (i > 0) {
                separator = ",&nbsp;&nbsp;";
            }

            $("#spanCcros").append($("<span />").html(separator + deleteButton +
                    String.format(ccroLink, String.format(URLS.VIEW_PROPERTY,
                            ApplicationCtrl.Application.properties[i].propId),
                            ApplicationCtrl.Application.properties[i].propNumber)));
        }

        // Show/hide search button
        if (!ApplicationCtrl.view &&
                (ApplicationCtrl.Application.properties.length < 1
                        || ApplicationCtrl.AppType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Surrender
                        || ApplicationCtrl.AppType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Termination)) {
            $("#lnkSearchCcro").show();
        } else {
            $("#lnkSearchCcro").hide();
        }
    }
};

ApplicationCtrl.showPropSearchDialog = function () {
    $("#propSearchDialog").modal('show');
    if (isNull(ApplicationCtrl.rightSearchControl)) {
        var selectPropFunc = function (prop) {
            if (!isNull(ApplicationCtrl.Application)) {
                if (isNull(ApplicationCtrl.Application.properties)) {
                    ApplicationCtrl.Application.properties = [];
                }
                // Check property in the list
                var found = false;
                for (var i = 0; i < ApplicationCtrl.Application.properties.length; i++) {
                    if (ApplicationCtrl.Application.properties[i] === prop.propId) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // Add into the list
                    var appProp = new ApplicationDao.ApplicationProperty();
                    appProp.propId = prop.propId;
                    appProp.propNumber = prop.propNumber;
                    ApplicationCtrl.Application.properties.push(appProp);
                    ApplicationCtrl.fillProperties();
                }
            }
            $("#propSearchDialog").modal('hide');
        };

        ApplicationCtrl.rightSearchControl = new Controls.RightSearch("ctrlRightSearch", "propSearch", {onSelect: selectPropFunc, height: 330});
        ApplicationCtrl.rightSearchControl.init();
        $("#propSearchDialog").on('shown.bs.modal', function () {
            $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
        });
    }
};

ApplicationCtrl.deleteProp = function (propId) {
    if (!isNullOrEmpty(propId)) {
        if (!isNull(ApplicationCtrl.Application) && !isNull(ApplicationCtrl.Application.properties)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                for (var i = 0; i < ApplicationCtrl.Application.properties.length; i++) {
                    if (ApplicationCtrl.Application.properties[i].propId === propId) {
                        ApplicationCtrl.Application.properties.splice(i, 1);
                        break;
                    }
                }
                ApplicationCtrl.fillProperties();
            });
        }
    }
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

ApplicationCtrl.openProperty = function () {
    window.location.replace(String.format(URLS.EDIT_PROPERTY, ApplicationCtrl.Application.id));
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

ApplicationCtrl.approve = function () {
    alertConfirm($.i18n("app-confirm-approve"), function () {
        ApplicationDao.approveApplication(ApplicationCtrl.Application.id, function () {
            // Redirect
            window.location.replace(
                    String.format(URLS.VIEW_APPLICATION_WITH_MESSAGE,
                            ApplicationCtrl.Application.id, ApplicationCtrl.MESSAGES.approved));
        });
    });
};

ApplicationCtrl.complete = function () {
    alertConfirm($.i18n("app-confirm-complete"), function () {
        ApplicationDao.completeApplication(ApplicationCtrl.Application.id, function () {
            // Redirect
            window.location.replace(
                    String.format(URLS.VIEW_APPLICATION_WITH_MESSAGE,
                            ApplicationCtrl.Application.id, ApplicationCtrl.MESSAGES.completed));
        });
    });
};

ApplicationCtrl.reject = function () {
    var reason = $("#txtReason").val();
    if (isNullOrEmpty(reason)) {
        alertErrorMessage($.i18n("err-app-reason-empty"));
        return;
    }
    ApplicationDao.rejectApplication(ApplicationCtrl.Application.id, reason, function () {
        $("#reasonDialog").modal('hide');
        // Redirect
        window.location.replace(String.format(URLS.VIEW_APPLICATION_WITH_MESSAGE, ApplicationCtrl.Application.id, ApplicationCtrl.MESSAGES.rejected));
    });
};

ApplicationCtrl.withdraw = function () {
    var reason = $("#txtReason").val();
    if (isNullOrEmpty(reason)) {
        alertErrorMessage($.i18n("err-app-reason-empty"));
        return;
    }
    ApplicationDao.withdrawApplication(ApplicationCtrl.Application.id, reason, function () {
        $("#reasonDialog").modal('hide');
        // Redirect
        window.location.replace(String.format(URLS.VIEW_APPLICATION_WITH_MESSAGE, ApplicationCtrl.Application.id, ApplicationCtrl.MESSAGES.withdrawn));
    });
};

ApplicationCtrl.showReasonDialog = function (evt) {
    if (!$("#reasonDialog").length) {
        $("body").append(
                '<div class="modal fade" id="reasonDialog" tabindex="-1" role="dialog" aria-hidden="true"> \
                        <div class="modal-dialog" style="width:400px;"> \
                            <div class="modal-content"> \
                                <div class="modal-header"> \
                                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">' + $.i18n("gen-close") + '</span></button> \
                                    <h4 class="modal-title">' + $.i18n("app-reason") + '</h4> \
                                </div> \
                                <div id="reasonDialogBody" class="modal-body" style="padding: 0px 5px 0px 5px;"> \
                                    <div class="content"> \
                                        <label>' + $.i18n("app-reason-text") + '</label>\
                                        <i class="glyphicon glyphicon-required"></i><br />\
                                        <textarea class="form-control" rows="3" id="txtReason"></textarea> \
                                    </div> \
                                </div> \
                                <div class="modal-footer" style="margin-top: 0px;padding: 15px 20px 15px 20px;"> \
                                    <button type="button" class="btn btn-default" data-dismiss="modal">' + $.i18n("gen-close") + '</button> \
                                    <button type="button" id="btnSendReason" class="btn btn-primary"></button> \
                                </div> \
                            </div> \
                        </div> \
                    </div>'
                );
    }

    $("#btnSendReason").off("click", ApplicationCtrl.reject);
    $("#btnSendReason").off("click", ApplicationCtrl.withdraw);

    if (evt.data.reject) {
        $("#btnSendReason").text($.i18n("gen-reject"));
        $("#btnSendReason").on("click", ApplicationCtrl.reject);
    } else {
        $("#btnSendReason").text($.i18n("gen-withdraw"));
        $("#btnSendReason").on("click", ApplicationCtrl.withdraw);
    }
    $("#txtReason").val("");
    $("#reasonDialog").modal('show');
};

ApplicationCtrl.validate = function () {
    var errors = [];
    // Check CCRO attached for transactions other than new registration
    if (ApplicationCtrl.AppType.transactionTypeCode.toLowerCase() !== RefDataDao.TRANSACTION_TYPE_CODES.FirstRegistration.toLowerCase()
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
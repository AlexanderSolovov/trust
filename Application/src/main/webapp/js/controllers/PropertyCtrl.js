/* global RefDataDao */

/**
 * Contains methods to manage map page. 
 * Requires SearchDao.js, Map.js, Global.js, URLS.js, PropertyDao.js, 
 * ApplicationDao.js, Pois.js
 */

var PropertyCtrl = PropertyCtrl || {};
PropertyCtrl.app = null;
PropertyCtrl.appType = null;
PropertyCtrl.allowedRightTypes = null;
PropertyCtrl.prop = null;
PropertyCtrl.parcel = null;
PropertyCtrl.rightId = null;
PropertyCtrl.selectedRight = null;
PropertyCtrl.selectedRightRow = null;
PropertyCtrl.tableRights;
PropertyCtrl.tableRightsHistoric;
PropertyCtrl.rightTypes = null;
PropertyCtrl.regStatuses = null;
PropertyCtrl.landUses = null;
PropertyCtrl.occupancyTypes = null;
PropertyCtrl.LegalEntities = null;
PropertyCtrl.Persons = null;
PropertyCtrl.Pois = null;

PropertyCtrl.MESSAGES = {
    saved: "saved"
};

// Load application information and show it
$(document).ready(function () {
    var appId = getUrlParam("appid");
    var propId = getUrlParam("id");
    PropertyCtrl.rightId = getUrlParam("rightid");

    if (isNull(appId) && isNull(propId) && isNull(PropertyCtrl.rightId)) {
        showErrorMessage($.i18n("err-prop-not-found"));
        return;
    }

    // Check user rights
    if ((!isNull(appId) && !Global.USER_PERMISSIONS.canManageRrr) || !Global.USER_PERMISSIONS.canView) {
        showErrorMessage($.i18n("err-forbidden"));
        return;
    }

    var fillParcel = function () {
        SearchDao.searchParcelById(PropertyCtrl.prop.parcelId, function (parcel) {
            if (isNull(parcel)) {
                showErrorMessage($.i18n("err-parcel-not-found"));
                return;
            }
            PropertyCtrl.parcel = parcel;
            PropertyCtrl.fillForm();
        });
    };

    // Show notification if any
    if (!isNull(getUrlParam("msg"))) {
        var messageCode = getUrlParam("msg");
        if (messageCode === PropertyCtrl.MESSAGES.saved) {
            showNotification($.i18n("prop-saved"));
        }
    }

    if (!isNullOrEmpty(appId)) {
        // Get application
        ApplicationDao.getApplication(appId, function (app) {
            if (isNull(app)) {
                showErrorMessage($.i18n("err-app-not-found"));
                return;
            }

            // Check application
            if (!app.permissions.canRegisterRight) {
                showErrorMessage(String.format($.i18n("err-forbidden"), app.appNumber));
                return;
            }

            PropertyCtrl.app = app;

            // Get Property
            if (!isNull(app.properties) && app.properties.length > 0) {
                // TODO: show popup if multiple properties
                PropertyDao.getProperty(app.properties[0].propId, function (prop) {
                    PropertyCtrl.prop = prop;
                    fillParcel();
                });
            } else {
                PropertyDao.getPropertiesByApplication(app.id, function (props) {
                    if (!isNull(props) && props.length > 0) {
                        // TODO: show popup if multiple properties
                        PropertyCtrl.prop = props[0];
                    } else {
                        PropertyCtrl.prop = new PropertyDao.Property();
                        PropertyCtrl.prop.applicationId = app.id;
                        PropertyCtrl.prop.statusCode = Global.STATUS.pending;
                    }

                    // Check for parcel
                    if (isNull(PropertyCtrl.prop.parcelId)) {
                        // Get parcel(s) by application
                        SearchDao.searchParcelsByApp(app.id, function (parcels) {
                            if (isNull(parcels) || parcels.length < 1) {
                                showErrorMessage(String.format($.i18n("err-app-no-parcels"), app.appNumber));
                                return;
                            }
                            // TODO: show parcel selection form if multiple parcels
                            PropertyCtrl.parcel = parcels[0];
                            PropertyCtrl.prop.parcelId = parcels[0].id;
                            PropertyCtrl.fillForm();
                        });
                    } else {
                        // Get parcel by id
                        fillParcel();
                    }
                });
            }
        });
    } else if (!isNull(propId)) {
        // Get property by id
        PropertyDao.getProperty(propId, function (prop) {
            if (isNull(prop)) {
                showErrorMessage($.i18n("err-prop-not-found"));
                return;
            }
            PropertyCtrl.prop = prop;
            // Get parcel by id
            fillParcel();
        });
    } else if (!isNull(PropertyCtrl.rightId)) {
        // Get property by id
        PropertyDao.getPropertyByRight(PropertyCtrl.rightId, function (prop) {
            if (isNull(prop)) {
                showErrorMessage($.i18n("err-prop-not-found"));
                return;
            }
            PropertyCtrl.prop = prop;
            // Get parcel by id
            fillParcel();
        });

    }
});

PropertyCtrl.fillForm = function () {
    var loadingProcesses = 0;
    var editbale = !isNull(PropertyCtrl.app);

    var showProp = function () {
        if (loadingProcesses > 0) {
            return;
        }
        PropertyCtrl.setTile();

        // Customize toolbars
        if (PropertyCtrl.prop.statusCode !== Global.STATUS.historic && isNull(PropertyCtrl.rightId)) {
            $("#propToolbar").show();
        } else {
            $("#propToolbar").hide();
        }

        $("#btnSave").hide();
        $("#btnBack").hide();
        $("#btnDelete").hide();
        $("#btnPrintAdjudicationForm").hide();
        $("#btnPrintCert").hide();
        $("#btnPrintTransactionSheet").hide();
        $("#divAddRight").hide();

        if (editbale) {
            $("#btnSave").show();
            $("#btnSave").on("click", PropertyCtrl.save);
            $("#btnBack").show();
            $("#btnBack").on("click", PropertyCtrl.backToApplication);
            PropertyCtrl.showHideAddRightButton();
            $("#lnkAddRight").on("click", PropertyCtrl.openAddRight);
        } else {
            if (PropertyCtrl.prop.statusCode === Global.STATUS.pending) {
                $("#btnPrintAdjudicationForm").show();
                $("#btnPrintAdjudicationForm").on("click", PropertyCtrl.printAdjudicationForm);
            }
            if (PropertyCtrl.prop.statusCode === Global.STATUS.current) {
                $("#btnPrintCert").show();
                $("#btnPrintCert").on("click", PropertyCtrl.printCert);
                $("#btnPrintTransactionSheet").show();
                $("#btnPrintTransactionSheet").on("click", PropertyCtrl.printTransactionSheet);
            }
        }

        // Localize
        $("#mainDiv").i18n();

        // Show div
        $("#mainDiv").show();

        // Show right tab if right id is provided 
        if (!isNullOrEmpty(PropertyCtrl.rightId) && !isNull(PropertyCtrl.prop) && !isNull(PropertyCtrl.prop.rights)) {
            for (var i = 0; i < PropertyCtrl.prop.rights.length; i++) {
                if (PropertyCtrl.prop.rights[i].id === PropertyCtrl.rightId) {
                    PropertyCtrl.openRight(PropertyCtrl.prop.rights[i], false);
                    break;
                }
            }
        }
    };

    // Populate property info
    if (!isNull(PropertyCtrl.prop.regDate)) {
        $("#lblPropRegDate").text(dateFormat(PropertyCtrl.prop.regDate));
    } else {
        $("#lblPropRegDate").text("");
    }
    if (!isNull(PropertyCtrl.prop.terminationDate)) {
        $("#lblPropTerminationDate").text(dateFormat(PropertyCtrl.prop.terminationDate));
    } else {
        $("#lblPropTerminationDate").text("");
    }
    $("#lblPropFileNumber").text(String.empty(PropertyCtrl.prop.fileNumber));

    // App to create
    var link = DataTablesUtility.getViewLinkNewWindow();
    if (!editbale) {
        link = DataTablesUtility.getViewLinkCurrentWindow();
    }

    if (editbale && PropertyCtrl.prop.applicationId === PropertyCtrl.app.id) {
        $("#lblPropCreatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id), PropertyCtrl.app.appNumber));
    } else {
        loadingProcesses += 1;
        ApplicationDao.getApplicationNumber(PropertyCtrl.prop.applicationId, function (appNumber) {
            $("#lblPropCreatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, appNumber.id), appNumber.appNumber));
            loadingProcesses -= 1;
            showProp();
        });
    }

    // App to terminate
    if (!isNull(PropertyCtrl.prop.endApplicationId)) {
        loadingProcesses += 1;
        ApplicationDao.getApplicationNumber(PropertyCtrl.prop.endApplicationId, function (appNumber) {
            $("#lblPropTerminatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, appNumber.id), appNumber.appNumber));
            loadingProcesses -= 1;
            showProp();
        });
    }

    // AppType
    if (!isNull(PropertyCtrl.app)) {
        // Get app type
        loadingProcesses += 1;
        RefDataDao.getRecord(RefDataDao.REF_DATA_TYPES.AppType.type, PropertyCtrl.app.appTypeCode, function (refData) {
            PropertyCtrl.appType = refData;
            loadingProcesses -= 1;
            showProp();
        });
        loadingProcesses += 1;
        RefDataDao.getRightTypesByAppType(PropertyCtrl.app.appTypeCode, function (allowedRightTypes) {
            PropertyCtrl.allowedRightTypes = allowedRightTypes;
            loadingProcesses -= 1;
            showProp();
        });
    }

    // Rights
    loadingProcesses += 1;
    RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.RightType.type, function (rightTypes) {
        PropertyCtrl.rightTypes = rightTypes;
        // Status
        loadingProcesses += 1;
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.RegStatus.type, function (regStatuses) {
            if (isNull(regStatuses) || regStatuses.length < 1) {
                showErrorMessage($.i18n("err-prop-status-not-found"));
                return;
            }
            loadingProcesses -= 2;

            // Set property status
            PropertyCtrl.regStatuses = regStatuses;
            $("#lblPropStatus").text(RefDataDao.getRefDataByCode(regStatuses, PropertyCtrl.prop.statusCode).val);

            // Configure right tables
            var cols = [
                {data: "rightTypeCode", title: $.i18n("gen-type")},
                {data: "regDate", title: $.i18n("prop-reg-date")},
                {data: "folioNumber", title: $.i18n("right-folio-number")},
                {data: "rightholders", title: $.i18n("right-rightholders")},
                {data: "statusCode", title: $.i18n("gen-status")}
            ];

            if (editbale) {
                cols.push({title: " "});
            }

            var getRightHolders = function (data) {
                var result = "";
                if (!isNull(data) && data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        if (!isNull(data[i].party)) {
                            if (result === "") {
                                result = data[i].party.fullName;
                            } else {
                                result = result + ", " + data[i].party.fullName;
                            }
                        }
                    }
                }
                return result;
            };

            var colsDef = [
                {
                    targets: 0,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        return String.format(DataTablesUtility.getViewLink(), "PropertyCtrl.viewRight($(this).parents('tr'));return false;", RefDataDao.getRefDataByCode(PropertyCtrl.rightTypes, data).val);
                    }
                },
                {
                    targets: 1,
                    width: "125px",
                    "render": function (data, type, row, meta) {
                        if (type === "display") {
                            if (!isNull(data)) {
                                return dateFormat(data, dateFormat.masks.default);
                            } else {
                                return "";
                            }
                        }
                        return data;
                    }
                },
                {
                    targets: 2,
                    "orderable": false
                },
                {
                    targets: 3,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        return getRightHolders(data);
                    }
                },
                {
                    targets: 4,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        var icon = "";
                        if (!isNullOrEmpty(row.terminationApplicationId)) {
                            icon = ' <i class="glyphicon glyphicon-trash" title="' + $.i18n("right-for-remove-discharge") + '"></i>';
                        }
                        return (RefDataDao.getRefDataByCode(PropertyCtrl.regStatuses, data).val) + icon;
                    }
                }
            ];

            if (editbale) {
                colsDef.push({
                    targets: 5,
                    width: "85px",
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        if (row.statusCode === Global.STATUS.pending) {
                            return String.format(DataTablesUtility.getEditLinkWithText(), "PropertyCtrl.editRight($(this).parents('tr'));return false;") +
                                    "<br>" +
                                    String.format(DataTablesUtility.getDeleteLinkWithText(), "PropertyCtrl.deleteRight($(this).parents('tr'));return false;");
                        } else if (row.statusCode === Global.STATUS.current) {
                            if (PropertyCtrl.canTransact(row.id)) {
                                if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Transfer) {
                                    return String.format(DataTablesUtility.getTransferLink(), "PropertyCtrl.transferRight($(this).parents('tr'));return false;");
                                }
                                if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Remove) {
                                    var removeLink = "";
                                    if (row.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Mortgage) {
                                        removeLink = DataTablesUtility.getDischargeLink();
                                    } else {
                                        removeLink = DataTablesUtility.getWithdrawLink();
                                    }
                                    return String.format(removeLink, "PropertyCtrl.terminateRight($(this).parents('tr'));return false;");
                                }
                                if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Vary) {
                                    return String.format(DataTablesUtility.getVaryLink(), "PropertyCtrl.varyRight($(this).parents('tr'));return false;");
                                }
                                if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Rectify) {
                                    return String.format(DataTablesUtility.getRectifyLink(), "PropertyCtrl.rectifyRight($(this).parents('tr'));return false;");
                                }
                            } else {
                                if (!isNullOrEmpty(row.terminationApplicationId)) {
                                    var cancelRemoveLink = "";
                                    if (row.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Mortgage) {
                                        cancelRemoveLink = DataTablesUtility.getCancelDischargeLink();
                                    } else {
                                        cancelRemoveLink = DataTablesUtility.getCancelWithdrawLink();
                                    }
                                    return String.format(cancelRemoveLink, "PropertyCtrl.cancelRightTermination($(this).parents('tr'));return false;");
                                }
                            }
                        }
                        return "";
                    }
                });
            }

            PropertyCtrl.tableRights = $("#tableActivePendingRights").DataTable({
                data: PropertyCtrl.getActiveRights(),
                "paging": false,
                "info": false,
                "sort": true,
                "searching": false,
                "scrollCollapse": true,
                "orderFixed": [[1, 'desc']],
                "order": [[1, 'desc']],
                "dom": '<"tableToolbar">frtip',
                language: DataTablesUtility.getLanguage(),
                columns: cols,
                columnDefs: colsDef
            });

            PropertyCtrl.tableRightsHistoric = $("#tableHistoricRights").DataTable({
                data: PropertyCtrl.getHistoricRights(),
                "paging": false,
                "info": false,
                "sort": true,
                "searching": false,
                "scrollCollapse": true,
                "orderFixed": [[1, 'desc']],
                "order": [[1, 'desc']],
                "dom": '<"tableToolbar">frtip',
                language: DataTablesUtility.getLanguage(),
                columns: [
                    {data: "rightTypeCode", title: $.i18n("gen-type")},
                    {data: "regDate", title: $.i18n("prop-reg-date")},
                    {data: "terminationDate", title: $.i18n("prop-termination-date")},
                    {data: "folioNumber", title: $.i18n("right-folio-number")},
                    {data: "rightholders", title: $.i18n("right-rightholders")}
                ],
                columnDefs: [
                    {
                        targets: 0,
                        "orderable": false,
                        "render": function (data, type, row, meta) {
                            return String.format(DataTablesUtility.getViewLink(), "PropertyCtrl.viewHistoricRight($(this).parents('tr'));return false;", RefDataDao.getRefDataByCode(PropertyCtrl.rightTypes, data).val);
                        }
                    },
                    {
                        targets: [1, 2],
                        "orderable": false,
                        width: "125px",
                        "render": function (data, type, row, meta) {
                            if (!isNull(data)) {
                                return dateFormat(data, dateFormat.masks.default);
                            } else {
                                return "";
                            }
                        }
                    },
                    {
                        targets: 3,
                        "orderable": false
                    },
                    {
                        targets: 4,
                        "orderable": false,
                        "render": function (data, type, row, meta) {
                            return getRightHolders(data);
                        }
                    }
                ]
            });

            showProp();
        }, null, null, true, true);
    }, null, null, true, true);

    // Populate parcel info
    $("#lblParcelUka").html(String.format(link, String.format(URLS.VIEW_MAP_WITH_PARCEL, PropertyCtrl.parcel.id), PropertyCtrl.parcel.uka));
    $("#lblParcelSurveyDate").text(dateFormat(PropertyCtrl.parcel.surveyDate));
    $("#lblParcelStatus").text(PropertyCtrl.parcel.statusName);
    $("#lblParcelLandType").text(String.empty(PropertyCtrl.parcel.landTypeName));
    $("#lblParcelLocation").text(String.empty(PropertyCtrl.parcel.parcelLocation));
    $("#lblParcelAddress").text(String.empty(PropertyCtrl.parcel.address));
    $("#lblPaprcelComments").text(String.empty(PropertyCtrl.parcel.comment));
    if (!isNull(PropertyCtrl.parcel.applicationId)) {
        $("#lblPaprcelCreatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, PropertyCtrl.parcel.applicationId), PropertyCtrl.parcel.appNumber));
    } else {
        $("#lblPaprcelCreatedByApp").text("");
    }
    if (!isNull(PropertyCtrl.parcel.endApplicationId)) {
        $("#lblParcelTerminatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, PropertyCtrl.parcel.endApplicationId), PropertyCtrl.parcel.endAppNumber));
    } else {
        $("#lblParcelTerminatedByApp").text("");
    }
};

PropertyCtrl.showHideAddRightButton = function () {
    $("#divAddRight").hide();
    if (!isNull(PropertyCtrl.appType) &&
            (PropertyCtrl.appType.transactionTypeCode.toLowerCase() === RefDataDao.TRANSACTION_TYPE_CODES.FirstRegistration.toLowerCase() ||
                    PropertyCtrl.appType.transactionTypeCode.toLowerCase() === RefDataDao.TRANSACTION_TYPE_CODES.Registration.toLowerCase())) {
        if (PropertyCtrl.getAllowedRightTypes().length > 0) {
            $("#divAddRight").show();
        }
    }
};

PropertyCtrl.getActiveRights = function () {
    var propRights = [];
    if (PropertyCtrl.tableRights) {
        PropertyCtrl.tableRights.rows().data().each(function (r) {
            propRights.push(r);
        });
    } else if (PropertyCtrl.prop.rights) {
        propRights = PropertyCtrl.prop.rights;
    }

    if (propRights.length < 1) {
        return [];
    }

    var result = [];
    for (var i = 0; i < propRights.length; i++) {
        if (propRights[i].statusCode === Global.STATUS.current || propRights[i].statusCode === Global.STATUS.pending) {
            result.push(propRights[i]);
        }
    }
    return result;
};

PropertyCtrl.getHistoricRights = function () {
    var propRights = [];
    if (PropertyCtrl.prop.rights) {
        propRights = PropertyCtrl.prop.rights;
    }

    if (propRights.length < 1) {
        return [];
    }

    var result = [];
    for (var i = 0; i < propRights.length; i++) {
        if (propRights[i].statusCode === Global.STATUS.historic) {
            result.push(propRights[i]);
        }
    }
    return result;
};

PropertyCtrl.getAllowedRightTypes = function () {
    var result = [];
    if (PropertyCtrl.allowedRightTypes !== null) {
        var activeRights = PropertyCtrl.getActiveRights();
        for (var i = 0; i < PropertyCtrl.allowedRightTypes.length; i++) {
            if (PropertyCtrl.allowedRightTypes[i].allowMultiple) {
                result.push(PropertyCtrl.allowedRightTypes[i]);
            } else {
                // Check for rights to exist
                var found = false;
                if (activeRights.length > 0) {
                    for (var j = 0; j < activeRights.length; j++) {
                        if (activeRights[j].rightTypeCode === PropertyCtrl.allowedRightTypes[i].code) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    result.push(PropertyCtrl.allowedRightTypes[i]);
                }
            }
        }
    }
    return result;
};

PropertyCtrl.canTransact = function (rightId) {
    if (PropertyCtrl.allowedRightTypes !== null &&
            (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Rectify ||
                    PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Transfer ||
                    PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Vary ||
                    PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Remove)) {
        var activeRights = PropertyCtrl.getActiveRights();
        // Check for rights to exist
        if (activeRights.length > 0) {
            for (var i = 0; i < activeRights.length; i++) {
                if (activeRights[i].id === rightId && activeRights[i].statusCode === Global.STATUS.current) {
                    // Check if right is already marked for termination
                    if (!isNullOrEmpty(activeRights[i].terminationApplicationId)) {
                        return false;
                    }

                    // Check if type is allowed
                    for (var j = 0; j < PropertyCtrl.allowedRightTypes.length; j++) {
                        if (activeRights[i].rightTypeCode === PropertyCtrl.allowedRightTypes[j].code) {
                            // Check if pending right with parent id already exists
                            for (var k = 0; k < activeRights.length; k++) {
                                if (activeRights[i].id === activeRights[k].parentId && activeRights[k].statusCode === Global.STATUS.pending) {
                                    return false;
                                }
                            }
                            // Can transact
                            return true;
                        }
                    }
                }
            }
        }
    }
    return false;
};

PropertyCtrl.setTile = function (right) {
    var propNumber;

    if (isNull(PropertyCtrl.prop.propNumber)) {
        propNumber = "#" + $.i18n("gen-new");
    } else {
        propNumber = "#" + PropertyCtrl.prop.propNumber;
    }
    document.title = document.title + " " + $.i18n("prop-prop") + " " + propNumber;
    if (!isNull(right)) {
        propNumber = propNumber + " (" + RefDataDao.getRefDataByCode(PropertyCtrl.rightTypes, right.rightTypeCode).val + ")";
    }
    $("#propNumber").text(propNumber);
};

PropertyCtrl.backToApplication = function () {
    // Check for changes
    var rights = [];
    var existingRights = [];
    if (PropertyCtrl.prop.rights !== null) {
        for (var i = 0; i < PropertyCtrl.prop.rights.length; i++) {
            if (isNullOrEmpty(PropertyCtrl.prop.rights[i].statusCode) || PropertyCtrl.prop.rights[i].statusCode === Global.STATUS.pending
                    || (PropertyCtrl.prop.rights[i].statusCode === Global.STATUS.current && !isNullOrEmpty(PropertyCtrl.prop.rights[i].terminationApplicationId))) {
                existingRights.push(PropertyCtrl.prop.rights[i]);
            }
        }
    }

    PropertyCtrl.tableRights.rows().data().each(function (d) {
        // Include only pending or for termination
        if (isNullOrEmpty(d.statusCode) || d.statusCode === Global.STATUS.pending
                || (d.statusCode === Global.STATUS.current && !isNullOrEmpty(d.terminationApplicationId))) {
            rights.push(d);
        }
    });

    var hasChanges = false;
    if (existingRights.length !== rights.length) {
        hasChanges = true;
    } else {
        // Check rights
        for (var i = 0; i < existingRights.length; i++) {
            if (JSON.stringify(existingRights[i]) !== JSON.stringify(rights[i])){
                hasChanges = true;
                break;
            }
        }
    }

    if (hasChanges) {
        alertConfirm($.i18n("gen-unsaved-changes"), function () {
            PropertyCtrl.save();
        }, function () {
            window.location.replace(String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id));
        });
    } else {
        window.location.replace(String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id));
    }
};

PropertyCtrl.printAdjudicationForm = function () {
    var w = window.open(Global.APP_ROOT + "/ws/" + Global.LANG + "/report/adjudicationform/" + PropertyCtrl.prop.id, 'AdjudicationForm', 'left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,menubar=no,status=no,replace=true');
    if (window.focus) {
        w.focus();
    }
};

PropertyCtrl.printCert = function () {
    var w = window.open(Global.APP_ROOT + "/ws/" + Global.LANG + "/report/certificate/" + PropertyCtrl.prop.id, 'Title', 'left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,menubar=no,status=no,replace=true');
    if (window.focus) {
        w.focus();
    }
};

PropertyCtrl.printTransactionSheet = function () {
    var w = window.open(Global.APP_ROOT + "/ws/" + Global.LANG + "/report/transactionsheet/" + PropertyCtrl.prop.id, 'TransactionSheet', 'left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,menubar=no,status=no,replace=true');
    if (window.focus) {
        w.focus();
    }
};

PropertyCtrl.viewHistoricRight = function (rowSelector) {
    PropertyCtrl.openRight(PropertyCtrl.tableRightsHistoric.row(rowSelector).data(), false);
};

PropertyCtrl.viewRight = function (rowSelector) {
    PropertyCtrl.selectedRightRow = PropertyCtrl.tableRights.row(rowSelector);
    PropertyCtrl.openRight(PropertyCtrl.tableRights.row(rowSelector).data(), false);
};

PropertyCtrl.editRight = function (rowSelector) {
    PropertyCtrl.selectedRightRow = PropertyCtrl.tableRights.row(rowSelector);
    PropertyCtrl.openRight(PropertyCtrl.tableRights.row(rowSelector).data(), true);
};

PropertyCtrl.deleteRight = function (rowSelector) {
    if (!isNull(rowSelector)) {
        alertConfirm($.i18n("gen-confirm-delete"), function () {
            // Remove from table 
            PropertyCtrl.tableRights.row(rowSelector).remove();
            PropertyCtrl.refreshRightsTable();
            PropertyCtrl.showHideAddRightButton();
        });
    }
};

PropertyCtrl.transferRight = function (rowSelector) {
    PropertyCtrl.selectedRightRow = null;
    // Copy right
    var right = JSON.parse(JSON.stringify(PropertyCtrl.tableRights.row(rowSelector).data()));
    // Reset parent right properties and set parent id
    right.parentId = right.id;
    right.id = null;
    right.folioNumber = null;
    right.regDate = null;
    if (PropertyCtrl.app) {
        right.applicationId = PropertyCtrl.app.id;
    }
    right.version = 0;
    right.statusCode = Global.STATUS.pending;
    right.occupancyTypeCode = null;
    right.dealAmount = null;
    right.rightholders = [];
    right.documents = [];
    right.pois = [];
    right.deceasedOwner = null;
    right.description = null;
    PropertyCtrl.openRight(right, true);
};

PropertyCtrl.varyRight = function (rowSelector) {
    PropertyCtrl.selectedRightRow = null;
    // Copy right
    var right = JSON.parse(JSON.stringify(PropertyCtrl.tableRights.row(rowSelector).data()));
    // Reset parent right properties and set parent id
    right.parentId = right.id;
    right.id = null;
    right.folioNumber = null;
    right.regDate = null;
    if (PropertyCtrl.app) {
        right.applicationId = PropertyCtrl.app.id;
    }
    right.version = 0;
    right.statusCode = Global.STATUS.pending;
    right.description = null;
    // Reset POIs
    if (!isNullOrEmpty(right.pois)) {
        for (var i = 0; i < right.pois.length; i++) {
            right.pois[i].id = null;
            right.pois[i].version = 0;
        }
    }
    PropertyCtrl.openRight(right, true);
};

PropertyCtrl.rectifyRight = function (rowSelector) {
    PropertyCtrl.selectedRightRow = null;
    // Copy right
    var right = JSON.parse(JSON.stringify(PropertyCtrl.tableRights.row(rowSelector).data()));
    // Reset parent right properties and set parent id
    right.parentId = right.id;
    right.id = null;
    right.folioNumber = null;
    right.regDate = null;
    if (PropertyCtrl.app) {
        right.applicationId = PropertyCtrl.app.id;
    }
    right.version = 0;
    right.statusCode = Global.STATUS.pending;
    right.description = null;
    // Reset POIs
    if (!isNullOrEmpty(right.pois)) {
        for (var i = 0; i < right.pois.length; i++) {
            right.pois[i].id = null;
            right.pois[i].version = 0;
        }
    }
    PropertyCtrl.openRight(right, true);
};

PropertyCtrl.terminateRight = function (rowSelector) {
    var right = PropertyCtrl.tableRights.row(rowSelector).data();
    if (PropertyCtrl.app) {
        right.terminationApplicationId = PropertyCtrl.app.id;
    }
    PropertyCtrl.refreshRightsTable();
};

PropertyCtrl.cancelRightTermination = function (rowSelector) {
    var right = PropertyCtrl.tableRights.row(rowSelector).data();
    right.terminationApplicationId = null;
    PropertyCtrl.refreshRightsTable();
};

PropertyCtrl.newRight = function (rightTypeCode) {
    if (isNull(rightTypeCode)) {
        rightTypeCode = $("#cbxRightTypes").val();
        if (isNull(rightTypeCode)) {
            return;
        }
    }

    $("#dialogAddRight").modal('hide');
    var right = new PropertyDao.Right();
    right.rightTypeCode = rightTypeCode;
    right.statusCode = Global.STATUS.pending;
    if (PropertyCtrl.app) {
        right.applicationId = PropertyCtrl.app.id;
    }
    PropertyCtrl.openRight(right, true);
};

PropertyCtrl.openRight = function (right, forEdit) {
    PropertyCtrl.selectedRight = right;
    PropertyCtrl.setTile(right);
    var allowRightholdersEditing = false;

    // Customize toolbar
    if (forEdit) {
        $("#btnSaveRight").show();
        if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.FirstRegistration ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Registration ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Rectify ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Transfer ||
                (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Vary &&
                        right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Mortgage)) {
            allowRightholdersEditing = true;
        }
    } else {
        $("#btnSaveRight").hide();
    }

    var rightholders = [];
    var rightType = RefDataDao.getRefDataByCode(PropertyCtrl.rightTypes, right.rightTypeCode);
    var isOwnership = rightType.rightTypeGroupCode === RefDataDao.RIGHT_TYPE_GROUP_CODES.Ownership;

    if (!isNull(right.rightholders)) {
        for (var i = 0; i < right.rightholders.length; i++) {
            // Add owner type related fields
            right.rightholders[i].party.ownerTypeCode = right.rightholders[i].ownerTypeCode;
            right.rightholders[i].party.shareSize = right.rightholders[i].shareSize;
            rightholders.push(right.rightholders[i].party);
        }
    }

    var loadingRight = 0;

    if (isOwnership) {
        loadingRight += 1;
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.LandUse.type, function (list) {
            PropertyCtrl.landUses = list;
            if ($("#cbxDeclaredLanduse > option").length < 1) {
                var activeLandUses = RefDataDao.filterActiveRecords(PropertyCtrl.landUses);
                populateSelectList(activeLandUses, "cbxDeclaredLanduse");
                populateSelectList(activeLandUses, "cbxApprovedLanduse");
            }
            $("#cbxDeclaredLanduse").val(right.declaredLanduseCode);
            $("#cbxApprovedLanduse").val(right.approvedLanduseCode);
            if (!isNull(right.declaredLanduseCode) && !isNull(PropertyCtrl.landUses)) {
                $("#lblDeclaredLanduse").text(RefDataDao.getRefDataByCode(PropertyCtrl.landUses, right.declaredLanduseCode).val);
            } else {
                $("#lblDeclaredLanduse").text("");
            }
            if (!isNull(right.approvedLanduseCode) && !isNull(PropertyCtrl.landUses)) {
                $("#lblApprovedLanduse").text(RefDataDao.getRefDataByCode(PropertyCtrl.landUses, right.approvedLanduseCode).val);
            } else {
                $("#lblApprovedLanduse").text("");
            }

            loadingRight -= 1;
            showRight();
        });

        loadingRight += 1;
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.OccupancyType.type, function (list) {
            PropertyCtrl.occupancyTypes = list;
            if ($("#cbxOccupancyType > option").length < 1) {
                populateSelectList(RefDataDao.filterActiveRecords(PropertyCtrl.occupancyTypes), "cbxOccupancyType");
            }

            $("#cbxOccupancyType").val(right.occupancyTypeCode);
            if (!isNull(right.occupancyTypeCode) && !isNull(PropertyCtrl.occupancyTypes)) {
                $("#lblOccupancyType").text(RefDataDao.getRefDataByCode(PropertyCtrl.occupancyTypes, right.occupancyTypeCode).val);
            } else {
                $("#lblOccupancyType").text("");
            }

            loadingRight -= 1;
            showRight();
        }, null, function () {
            PropertyCtrl.occupancySelected();
        }, true, true);
    }

    if (isNull(PropertyCtrl.LegalEntities)) {
        loadingRight += 1;
        PropertyCtrl.LegalEntities = new Controls.LegalEntities(
                "leRightholders", "divRightholderLegalEntity",
                {legalEntities: rightholders, editable: allowRightholdersEditing, app: PropertyCtrl.app}
        );
        PropertyCtrl.LegalEntities.init(function () {
            loadingRight -= 1;
            showRight();
        });
    } else {
        PropertyCtrl.LegalEntities.setEditable(allowRightholdersEditing);
        PropertyCtrl.LegalEntities.setLegalEntities(rightholders);
    }

    if (isNull(PropertyCtrl.Persons)) {
        loadingRight += 1;
        PropertyCtrl.Persons = new Controls.Persons(
                "personRightholders", "divRightholderPerson",
                {persons: rightholders, editable: allowRightholdersEditing, app: PropertyCtrl.app, isOwnership: isOwnership}
        );
        PropertyCtrl.Persons.init(function () {
            loadingRight -= 1;
            showRight();
        });
    } else {
        PropertyCtrl.Persons.setEditable(allowRightholdersEditing);
        PropertyCtrl.Persons.setPersons(rightholders);
    }

    if (isNull(PropertyCtrl.RightDocs)) {
        loadingRight += 1;
        PropertyCtrl.RightDocs = new Controls.Documents(
                "rightDocs", "divRightDocs",
                {editable: forEdit, documents: makeObjectsList(right.documents, "document"), app: PropertyCtrl.app}
        );
        PropertyCtrl.RightDocs.init(function () {
            loadingRight -= 1;
            showRight();
        });
    } else {
        PropertyCtrl.RightDocs.setEditable(forEdit);
        PropertyCtrl.RightDocs.setDocuments(makeObjectsList(right.documents, "document"));
    }

    if (isNull(PropertyCtrl.Pois)) {
        loadingRight += 1;
        PropertyCtrl.Pois = new Controls.Pois("rightPois", "divPois", {pois: right.pois, editable: forEdit});
        PropertyCtrl.Pois.init(function () {
            loadingRight -= 1;
            showRight();
        });
    } else {
        PropertyCtrl.Pois.setEditable(forEdit);
        PropertyCtrl.Pois.setPois(right.pois);
    }

    // Populate main tab info

    if (!isNull(right.regDate)) {
        $("#lblRightRegDate").text(dateFormat(right.regDate));
    } else {
        $("#lblRightRegDate").text("");
    }
    if (!isNull(right.terminationDate)) {
        $("#lblRightTerminationDate").text(dateFormat(right.terminationDate));
    } else {
        $("#lblRightTerminationDate").text("");
    }

    if (!isNull(right.statusCode)) {
        $("#lblRightStatus").text(RefDataDao.getRefDataByCode(PropertyCtrl.regStatuses, right.statusCode).val);
    } else {
        $("#lblRightStatus").text("");
    }

    // App to create
    var link = DataTablesUtility.getViewLinkNewWindow();
    if (!forEdit) {
        link = DataTablesUtility.getViewLinkCurrentWindow();
    }

    if (!isNull(PropertyCtrl.app) && right.applicationId === PropertyCtrl.app.id) {
        $("#lblRightCreatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id), PropertyCtrl.app.appNumber));
    } else {
        loadingRight += 1;
        ApplicationDao.getApplicationNumber(right.applicationId, function (appNumber) {
            $("#lblRightCreatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, appNumber.id), appNumber.appNumber));
            loadingRight -= 1;
            showRight();
        });
    }

    // App to terminate
    if (!isNull(PropertyCtrl.app) && right.endApplicationId === PropertyCtrl.app.id) {
        $("#lblRightTerminatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id), PropertyCtrl.app.appNumber));
    } else if (!isNull(right.endApplicationId)) {
        loadingRight += 1;
        ApplicationDao.getApplicationNumber(right.endApplicationId, function (appNumber) {
            $("#lblRightTerminatedByApp").html(String.format(link, String.format(URLS.VIEW_APPLICATION, appNumber.id), appNumber.appNumber));
            loadingRight -= 1;
            showRight();
        });
    }

    $("#lblFolioNumber").text(String.empty(right.folioNumber));
    $("#txtFolioNumber").val(String.empty(right.folioNumber));

    if (!isNull(right.allocationDate)) {
        $("#lblAllocationDate").text(dateFormat(right.allocationDate));
        $("#txtAllocationDate").val(dateFormat(right.allocationDate));
    } else {
        $("#txtAllocationDate").val("");
        $("#lblAllocationDate").text("");
    }

    if (!isNull(right.startDate)) {
        $("#txtStartDate").val(dateFormat(right.startDate));
        $("#lblStartDate").text(dateFormat(right.startDate));
    } else {
        $("#txtStartDate").val("");
        $("#lblStartDate").text("");
    }

    if (!isNull(right.endDate)) {
        $("#txtEndDate").val(dateFormat(right.endDate));
        $("#lblEndDate").text(dateFormat(right.endDate));
    } else {
        $("#txtEndDate").val("");
        $("#lblEndDate").text("");
    }

    var setDoubleField = function (name) {
        var htmlName = name.charAt(0).toUpperCase() + name.slice(1);
        if (!isNull(right[name]) && right[name] !== 0) {
            if (right[name] % 1 === 0) {
                right[name] = Math.floor(right[name]);
            }
            $("#txt" + htmlName).val(right[name]);
            $("#lbl" + htmlName).text(right[name]);
        } else {
            $("#txt" + htmlName).val("");
            $("#lbl" + htmlName).text("");
        }
    };

    setDoubleField("duration");
    setDoubleField("annualFee");
    setDoubleField("interesetRate");
    setDoubleField("dealAmount");

    $("#lblWitness1").text(String.empty(right.witness1));
    $("#txtWitness1").val(String.empty(right.witness1));

    $("#lblWitness2").text(String.empty(right.witness2));
    $("#txtWitness2").val(String.empty(right.witness2));

    $("#lblAdjudicator1").text(String.empty(right.adjudicator1));
    $("#txtAdjudicator1").val(String.empty(right.adjudicator1));

    $("#lblAdjudicator2").text(String.empty(right.adjudicator2));
    $("#txtAdjudicator2").val(String.empty(right.adjudicator2));

    $("#txtNeighborNorth").val(String.empty(right.neighborNorth));
    $("#lblNeighborNorth").text(String.empty(right.neighborNorth));

    $("#txtNeighborSouth").val(String.empty(right.neighborSouth));
    $("#lblNeighborSouth").text(String.empty(right.neighborSouth));

    $("#txtNeighborEast").val(String.empty(right.neighborEast));
    $("#lblNeighborEast").text(String.empty(right.neighborEast));

    $("#txtNeighborWest").val(String.empty(right.neighborWest));
    $("#lblNeighborWest").text(String.empty(right.neighborWest));

    $("#txtRightDescription").val(String.empty(right.description));
    $("#lblRightDescription").text(String.empty(right.description));

    var deceasedOwner = isNull(right.deceasedOwner) ? {} : right.deceasedOwner;

    $("#txtDeceasedFirstName").val(String.empty(deceasedOwner.firstName));
    $("#lblDeceasedFirstName").text(String.empty(deceasedOwner.firstName));

    $("#txtDeceasedMiddleName").val(String.empty(deceasedOwner.middleName));
    $("#lblDeceasedMiddleName").text(String.empty(deceasedOwner.middleName));

    $("#txtDeceasedLastName").val(String.empty(deceasedOwner.lastName));
    $("#lblDeceasedLastName").text(String.empty(deceasedOwner.lastName));

    $("#txtDeceasedDescription").val(String.empty(deceasedOwner.description));
    $("#lblDeceasedDescription").text(String.empty(deceasedOwner.description));

    // Hide fields which will be shown later by right type
    $("#divInteresetRate").hide();
    $("#divDealAmount").hide();
    $("#rowAdjudicators").hide();
    $("#rowNeigbors").hide();
    $("#divOccupancyType").hide();
    $("#divAllocationDate").hide();
    $("#divAnnualFee").hide();
    $("#tabPoisHeader").hide();
    $("#tabPoisHeader").hide();
    $("#divLegalEntity").hide();
    $("#divWitness1").hide();
    $("#divWitness2").hide();
    $("#divEndDate").hide();
    $("#divDuration").hide();

    showRight = function () {
        if (loadingRight > 0) {
            return;
        }

        PropertyCtrl.showPropPanel(false);

        if (forEdit) {
            $("#rightDiv .glyphicon-required").show();
            $("#rightDiv .form-control").show();
            $("#rightDiv .input-group").show();
            $("#rightDiv [id^=lbl]").hide();
        } else {
            $("#rightDiv .glyphicon-required").hide();
            $("#rightDiv .form-control").hide();
            $("#rightDiv .input-group").hide();
            $("#rightDiv [id^=lbl]").show();
        }

        // Enable disable based on transaction type
        if (forEdit) {
            if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Transfer ||
                    PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Vary) {
                $("#tabRightMain .form-control").prop('disabled', true);

                $("#txtFolioNumber").prop('disabled', false);
                $("#txtRightDescription").prop('disabled', false);

                if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Vary) {
                    $("#txtStartDate").prop('disabled', false);
                    $("#txtEndDate").prop('disabled', false);
                    $("#txtDuration").prop('disabled', false);
                    $("#txtAnnualFee").prop('disabled', false);
                    $("#txtInteresetRate").prop('disabled', false);
                    $("#txtDealAmount").prop('disabled', false);
                    $("#cbxApprovedLanduse").prop('disabled', false);
                }
            }
        }

        // Show fields based on the right type
        $("#reqDuration").hide();

        if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Ccro) {
            $("#rowAdjudicators").show();
            $("#rowNeigbors").show();
            $("#divOccupancyType").show();
            if (forEdit && allowRightholdersEditing) {
                $("#cbxOccupancyType").show();
                $("#lblOccupancyType").hide();
            } else {
                $("#cbxOccupancyType").hide();
                $("#lblOccupancyType").show();
            }
            $("#divAllocationDate").show();
            $("#divWitness1").show();
            $("#divWitness2").show();
            $("#divAnnualFee").show();
            $("#tabPoisHeader").show();
            $("#divDuration").show();
        } else if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Mortgage) {
            $("#divInteresetRate").show();
            $("#divDuration").show();
            if (forEdit) {
                $("#reqDuration").show();
            }
            $("#divDealAmount").show();
            $("#divLegalEntity").show();
            $("#divPerson").hide();
        } else if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Caveat) {
            $("#divEndDate").show();
            $("#divLegalEntity").show();
            $("#divPerson").show();
        }
    };

    showRight();
};

PropertyCtrl.showPropPanel = function (show) {
    if (show) {
        $("#rightDiv").hide();
        $("#propDiv").show();
        PropertyCtrl.showHideAddRightButton();
        PropertyCtrl.setTile();
        //$("a[href='#tabPropMain']").tab('show');
    } else {
        $("#propDiv").hide();
        $("#rightDiv").show();
        $("a[href='#tabRightMain']").tab('show');
    }
};

PropertyCtrl.occupancySelected = function () {
    var oType = $("#cbxOccupancyType").val();

    $("#divPerson").hide();
    $("#divLegalEntity").hide();
    $("#divDeceasedPerson").hide();

    if (isNullOrEmpty(oType)) {
        return;
    }

    if (oType === RefDataDao.OCCUPANCY_TYPE_CODES.Probate) {
        $("#divDeceasedPerson").show();
        $("#divPerson").show();
    } else if (oType === RefDataDao.OCCUPANCY_TYPE_CODES.NonNatural) {
        $("#divLegalEntity").show();
    } else {
        $("#divPerson").show();
    }
};

PropertyCtrl.openAddRight = function () {
    // If multiple rights can be added, show popup window
    var rightTypes = PropertyCtrl.getAllowedRightTypes();
    if (rightTypes.length > 1) {
        if (!$("#dialogAddRight").length) {
            $("#divAddRight").append('<div class="modal fade" id="dialogAddRight" tabindex="-1" role="dialog" aria-hidden="true"> \
                        <div class="modal-dialog" style="width:300px;"> \
                            <div class="modal-content"> \
                                <div class="modal-header"> \
                                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only" data-i18n="gen-close"></span></button> \
                                    <h4 class="modal-title" data-i18n="right-add-right"></h4> \
                                </div> \
                                <div class="modal-body" style="padding: 0px 5px 0px 5px;"> \
                                    <div class="content"> \
                                        <select id="cbxRightTypes" class="form-control"></select> \
                                    </div> \
                                </div> \
                                <div class="modal-footer" style="margin-top: 0px;padding: 15px 20px 15px 20px;"> \
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="gen-close"></button> \
                                    <button type="button" class="btn btn-primary" onclick="PropertyCtrl.newRight(null)" data-i18n="gen-select"></button> \
                                </div> \
                            </div> \
                        </div> \
                    </div>');
            $("#dialogAddRight").i18n();
        }
        $("#cbxRightTypes").empty();
        $.each(rightTypes, function (i, rightType) {
            $("#cbxRightTypes").append($("<option />").val(rightType.code).text(rightType.val));
        });
        $("#dialogAddRight").modal('show');
    } else {
        if (rightTypes.length > 0) {
            PropertyCtrl.newRight(rightTypes[0].code);
        }
    }
};

PropertyCtrl.backToProp = function () {
    if (JSON.stringify(PropertyCtrl.selectedRight) !== JSON.stringify(PropertyCtrl.prepareRight())) {
        alertConfirm($.i18n("gen-unsaved-changes"), function () {
            PropertyCtrl.saveRight();
        }, function () {
            PropertyCtrl.showPropPanel(true);
        });
    } else {
        PropertyCtrl.showPropPanel(true);
    }
};

PropertyCtrl.prepareRight = function () {
    var right = PropertyCtrl.selectedRight;

    var result = new PropertyDao.Right();
    result.id = right.id;
    result.rightTypeCode = right.rightTypeCode;
    result.regDate = right.regDate;
    result.terminationDate = right.terminationDate;
    result.parentId = right.parentId;
    result.applicationId = right.applicationId;
    result.endApplicationId = right.endApplicationId;
    result.statusCode = right.statusCode;
    result.version = right.version;
    result.witness3 = right.witness3;

    // General attributes
    setStringObjectProperty(right, result, "folioNumber", "txtFolioNumber");
    setDateObjectProperty(right, result, "allocationDate", "txtAllocationDate");
    setDateObjectProperty(right, result, "startDate", "txtStartDate");
    setDateObjectProperty(right, result, "endDate", "txtEndDate");
    setFloatObjectProperty(right, result, "duration", "txtDuration");
    setFloatObjectProperty(right, result, "annualFee", "txtAnnualFee");
    setFloatObjectProperty(right, result, "interesetRate", "txtInteresetRate");
    setFloatObjectProperty(right, result, "dealAmount", "txtDealAmount");
    setStringObjectProperty(right, result, "witness1", "txtWitness1");
    setStringObjectProperty(right, result, "witness2", "txtWitness2");
    setStringObjectProperty(right, result, "adjudicator1", "txtAdjudicator1");
    setStringObjectProperty(right, result, "adjudicator2", "txtAdjudicator2");
    setStringObjectProperty(right, result, "neighborNorth", "txtNeighborNorth");
    setStringObjectProperty(right, result, "neighborSouth", "txtNeighborSouth");
    setStringObjectProperty(right, result, "neighborEast", "txtNeighborEast");
    setStringObjectProperty(right, result, "neighborWest", "txtNeighborWest");
    setStringObjectProperty(right, result, "description", "txtRightDescription");
    setStringObjectProperty(right, result, "declaredLanduseCode", "cbxDeclaredLanduse");
    setStringObjectProperty(right, result, "approvedLanduseCode", "cbxApprovedLanduse");

    result.documents = makeVersionedList(right.documents, PropertyCtrl.RightDocs.getDocuments(), "document");

    if (RefDataDao.RIGHT_TYPE_CODES.Ccro === PropertyCtrl.selectedRight.rightTypeCode) {
        setStringObjectProperty(right, result, "occupancyTypeCode", "cbxOccupancyType");

        // Set deceased person to null if selected occupancy type is not probate
        if (result.occupancyTypeCode !== RefDataDao.OCCUPANCY_TYPE_CODES.Probate) {
            result.deceasedOwner = null;
        } else {
            // Assemble deceased person
            result.deceasedOwner = new PropertyDao.DeceasedPerson();
            if (!isNull(right.deceasedOwner)) {
                result.deceasedOwner.id = right.deceasedOwner.id;
                result.deceasedOwner.version = right.deceasedOwner.version;
            }
            setStringObjectProperty(right.deceasedOwner, result.deceasedOwner, "firstName", "txtDeceasedFirstName");
            setStringObjectProperty(right.deceasedOwner, result.deceasedOwner, "lastName", "txtDeceasedLastName");
            setStringObjectProperty(right.deceasedOwner, result.deceasedOwner, "middleName", "txtDeceasedMiddleName");
            setStringObjectProperty(right.deceasedOwner, result.deceasedOwner, "description", "txtDeceasedDescription");
        }

        // POIs
        result.pois = PropertyCtrl.Pois.getPois();
    }

    // Rightholders
    result.rightholders = [];
    if (isNull(result.occupancyTypeCode) || result.occupancyTypeCode !== RefDataDao.OCCUPANCY_TYPE_CODES.NonNatural) {
        result.rightholders = makeVersionedList(right.rightholders, PropertyCtrl.Persons.getPersons(), "party");
    }
    if (isNull(result.occupancyTypeCode) || result.occupancyTypeCode === RefDataDao.OCCUPANCY_TYPE_CODES.NonNatural) {
        var legalEntities = makeVersionedList(right.rightholders, PropertyCtrl.LegalEntities.getLegalEntities(), "party");

        if (!isNull(result.rightholders)) {
            result.rightholders = result.rightholders.concat(legalEntities);
        } else {
            result.rightholders = legalEntities;
        }
    }

    if (!isNull(result.rightholders)) {
        for (var i = 0; i < result.rightholders.length; i++) {
            // Update share and owner type
            result.rightholders[i].ownerTypeCode = result.rightholders[i].party.ownerTypeCode;
            result.rightholders[i].shareSize = result.rightholders[i].party.shareSize;
        }
    }
    return result;
};

PropertyCtrl.saveRight = function () {
    if (isNull(PropertyCtrl.selectedRight)) {
        return;
    }

    if (!PropertyCtrl.validateRight(PropertyCtrl.selectedRight, true)) {
        return;
    }

    var result = PropertyCtrl.prepareRight();

    // Update table with rights
    var currentRow;

    if (PropertyCtrl.selectedRightRow === null) {
        currentRow = PropertyCtrl.tableRights.row.add(result).draw().node();
    } else {
        // Update row
        currentRow = PropertyCtrl.selectedRightRow.data(result).draw().node();
    }

    PropertyCtrl.refreshRightsTable();

    // Animate changed/added row
    PropertyCtrl.showPropPanel(true);
    highlight(currentRow);
};

PropertyCtrl.refreshRightsTable = function () {
    PropertyCtrl.tableRights.rows().invalidate("data");
    PropertyCtrl.tableRights.draw(false);
};

PropertyCtrl.validateRight = function (right, showErrors) {
    var errors = [];

    // Get rightholders
    var persons = PropertyCtrl.Persons.getPersons();
    var les = PropertyCtrl.LegalEntities.getLegalEntities();

    if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Ccro) {
        if (isNullOrEmpty($("#txtAllocationDate").val())) {
            errors.push($.i18n("err-right-allocation-date-empty"));
        } else {
            if (DateUtility.isFuture($("#txtAllocationDate").datepicker("getDate"))) {
                errors.push($.i18n("err-right-allocation-date-in-future"));
            }
        }
        if (isNullOrEmpty($("#txtStartDate").val())) {
            errors.push($.i18n("err-right-start-date-empty"));
        }
        if (isNullOrEmpty($("#cbxDeclaredLanduse").val())) {
            errors.push($.i18n("err-right-declared-landuse-empty"));
        }
        if (isNullOrEmpty($("#cbxApprovedLanduse").val())) {
            errors.push($.i18n("err-right-approved-landuse-empty"));
        }
        if (isNullOrEmpty($("#txtAdjudicator1").val())) {
            errors.push($.i18n("err-right-adjudicator1-empty"));
        }
        if (isNullOrEmpty($("#txtAdjudicator2").val())) {
            errors.push($.i18n("err-right-adjudicator2-empty"));
        }
        if (isNullOrEmpty($("#txtNeighborNorth").val())) {
            errors.push($.i18n("err-right-north-empty"));
        }
        if (isNullOrEmpty($("#txtNeighborSouth").val())) {
            errors.push($.i18n("err-right-south-empty"));
        }
        if (isNullOrEmpty($("#txtNeighborEast").val())) {
            errors.push($.i18n("err-right-east-empty"));
        }
        if (isNullOrEmpty($("#txtNeighborWest").val())) {
            errors.push($.i18n("err-right-west-empty"));
        }
        if (isNullOrEmpty($("#cbxOccupancyType").val())) {
            errors.push($.i18n("err-right-occupancy-empty"));
        }

        // Check occupancy types
        var occupancyType = $("#cbxOccupancyType").val();

        if (!isNullOrEmpty(occupancyType)) {
            // Age, share, type
            var owners = 0;
            var admins = 0;
            var guardians = 0;
            var minors = 0;

            if (!isNull(persons)) {
                for (var i = 0; i < persons.length; i++) {
                    // Share
                    if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Common && isNullOrEmpty(persons[i].shareSize)) {
                        errors.push(String.format($.i18n("err-right-share-empty"), persons[i].fullName));
                    }

                    // Age
                    var age = DateUtility.getAge(persons[i].dob);
                    if (age < 18) {
                        if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Guardian &&
                                persons[i].ownerTypeCode === RefDataDao.OWNER_TYPE_CODES.Owner) {
                            minors += 1;
                        } else {
                            errors.push(String.format($.i18n("err-right-young-owner"), persons[i].fullName));
                        }
                    }

                    // Types
                    if (persons[i].ownerTypeCode === RefDataDao.OWNER_TYPE_CODES.Guardian) {
                        guardians += 1;
                    }
                    if (persons[i].ownerTypeCode === RefDataDao.OWNER_TYPE_CODES.Owner) {
                        owners += 1;
                    }
                    if (persons[i].ownerTypeCode === RefDataDao.OWNER_TYPE_CODES.Administrator) {
                        admins += 1;
                    }
                }
            }

            // Owner types and number
            if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Single) {
                if (owners < 1 || guardians > 0 || admins > 0) {
                    errors.push($.i18n("err-right-one-owner"));
                }
            }

            if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Probate) {
                if (admins < 1 || owners > 0 || guardians > 0) {
                    errors.push($.i18n("err-right-one-or-many-admins"));
                }

                if (isNullOrEmpty($("#txtDeceasedFirstName").val())) {
                    errors.push($.i18n("err-right-dp-first-name-empty"));
                }
                if (isNullOrEmpty($("#txtDeceasedLastName").val())) {
                    errors.push($.i18n("err-right-dp-last-name-empty"));
                }
            }

            if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Common ||
                    occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Joint) {
                if (owners < 2 || guardians > 0 || admins > 0) {
                    errors.push($.i18n("err-right-many-owners"));
                }
            }

            if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.Guardian) {
                if (owners < 1 || guardians < 1 || minors < 1 || admins > 0) {
                    errors.push($.i18n("err-right-wrong-guardianship"));
                }
            }

            if (occupancyType === RefDataDao.OCCUPANCY_TYPE_CODES.NonNatural) {
                if (les === null || les.length < 1) {
                    errors.push($.i18n("err-right-no-le"));
                } else if (les.length > 1) {
                    errors.push($.i18n("err-right-one-le"));
                }
            }
        }
    } else {
        if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Mortgage) {
            if (isNullOrEmpty($("#txtStartDate").val())) {
                errors.push($.i18n("err-right-start-date-empty"));
            }
            if (isNullOrEmpty($("#txtDuration").val())) {
                errors.push($.i18n("err-right-no-duraion"));
            }
        }

        if (right.rightTypeCode === RefDataDao.RIGHT_TYPE_CODES.Caveat) {
            if (isNullOrEmpty($("#txtStartDate").val())) {
                errors.push($.i18n("err-right-start-date-empty"));
            } else {
                if (!isNullOrEmpty($("#txtEndDate").val())) {
                    if (DateUtility.compareDates($("#txtStartDate").datepicker("getDate"), $("#txtEndDate").datepicker("getDate")) < 0) {
                        errors.push($.i18n("err-right-enddate-less-startdate"));
                    }
                }
            }
        }

        // Require rightholders for other types of right
        if ((les === null || les.length < 1) && (persons === null || persons.length < 1)) {
            errors.push($.i18n("err-right-no-rightholders"));
        }
    }

    if (errors.length > 0) {
        if (showErrors) {
            alertErrorMessages(errors);
        }
        return false;
    }

    return true;
};

PropertyCtrl.save = function () {
    var rights = [];

    PropertyCtrl.tableRights.rows().data().each(function (d) {
        // Include only pending or for termination
        if (isNullOrEmpty(d.statusCode) || d.statusCode === Global.STATUS.pending
                || (d.statusCode === Global.STATUS.current && !isNullOrEmpty(d.terminationApplicationId))) {
            rights.push(d);
        }
    });
    if (rights.length < 1) {
        alertErrorMessage($.i18n("err-right-no-rights"));
        return;
    }

    PropertyCtrl.prop.rights = rights;
    PropertyDao.saveProperty(PropertyCtrl.prop, function (prop) {
        // Redirect
        window.location.replace(String.format(URLS.VIEW_PROPERTY_WITH_MESSAGE, prop.id, PropertyCtrl.MESSAGES.saved));
    });
};
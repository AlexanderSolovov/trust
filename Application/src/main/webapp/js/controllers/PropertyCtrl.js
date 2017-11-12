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
PropertyCtrl.tableRights;
PropertyCtrl.rightTypes = null;
PropertyCtrl.regStatuses = null;
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
    PropertyCtrl.rightId = getUrlParam("rightId");

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

        // ====================== REMOVE ============================ //
        var right = new PropertyDao.Right();
        right.rightTypeCode = "ccro";
        right.statusCode = Global.STATUS.pending;
        PropertyCtrl.openRight(right, true);
        $('#tabPropRights').trigger('click')
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
    var link = "<a href='{0}' target='_blank'>#{1}</a>";
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
            for (var i = 0; i < regStatuses.length; i++) {
                if (regStatuses[i].code === PropertyCtrl.prop.statusCode) {
                    $("#lblPropStatus").text(regStatuses[i].val);
                    break;
                }
            }

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

            var getViewRightLink = function (data) {
                return String.format(DataTablesUtility.getViewLink(), "PropertyCtrl.viewRight($(this).parents('tr'));return false;", RefDataDao.getRefDataByCode(PropertyCtrl.rightTypes, data).val);
            };

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
                    "render": function (data, type, row, meta) {
                        return getViewRightLink(data);
                    }
                },
                {
                    targets: 1,
                    width: "125px",
                    "render": function (data, type, row, meta) {
                        if (!isNull(data)) {
                            return dateFormat(data, dateFormat.masks.dateTime);
                        } else {
                            return "";
                        }
                    }
                },
                {
                    targets: 3,
                    "render": function (data, type, row, meta) {
                        return getRightHolders(data);
                    }
                },
                {
                    targets: 4,
                    "render": function (data, type, row, meta) {
                        return RefDataDao.getRefDataByCode(PropertyCtrl.regStatuses, data).val;
                    }
                }
            ];

            if (editbale) {
                colsDef.push({
                    targets: 4,
                    "render": function (data, type, row, meta) {
                        return RefDataDao.getRefDataByCode(PropertyCtrl.regStatuses, data).val;
                    }
                });
            }

            PropertyCtrl.tableRights = $("#tableActivePendingRights").DataTable({
                data: PropertyCtrl.getRightsByStatus([Global.STATUS.current, Global.STATUS.pending]),
                "paging": false,
                "info": false,
                "sort": false,
                "searching": false,
                "scrollCollapse": true,
                "order": [[1, 'desc']],
                "dom": '<"tableToolbar">frtip',
                language: DataTablesUtility.getLanguage(),
                columns: cols,
                columnDefs: colsDef
            });

            $("#tableHistoricRights").DataTable({
                data: PropertyCtrl.getRightsByStatus([Global.STATUS.historic]),
                "paging": false,
                "info": false,
                "sort": false,
                "searching": false,
                "scrollCollapse": true,
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
                        "render": function (data, type, row, meta) {
                            return getViewRightLink(data);
                        }
                    },
                    {
                        targets: [1, 2],
                        width: "125px",
                        "render": function (data, type, row, meta) {
                            if (!isNull(data)) {
                                return dateFormat(data, dateFormat.masks.dateTime);
                            } else {
                                return "";
                            }
                        }
                    },
                    {
                        targets: 4,
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

PropertyCtrl.getRightsByStatus = function (statusCodes) {
    if (isNull(PropertyCtrl.prop.rights) || PropertyCtrl.prop.rights.length < 1) {
        return [];
    }

    var result = [];
    for (var i = 0; i < PropertyCtrl.prop.rights.length; i++) {
        if (statusCodes.indexOf(PropertyCtrl.prop.rights[i].statusCode) > -1) {
            result.push(PropertyCtrl.prop.rights[i]);
        }
    }
    return result;
};

PropertyCtrl.getAllowedRightTypes = function () {
    var result = [];
    if (PropertyCtrl.allowedRightTypes !== null) {
        var activeRights = PropertyCtrl.getRightsByStatus([Global.STATUS.current, Global.STATUS.pending]);
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
    window.location.replace(String.format(URLS.VIEW_APPLICATION, PropertyCtrl.app.id));
};

PropertyCtrl.printAdjudicationForm = function () {

};

PropertyCtrl.printCert = function () {

};

PropertyCtrl.printTransactionSheet = function () {

};

PropertyCtrl.viewRight = function (rowSelector) {
    PropertyCtrl.openRight(PropertyCtrl.tableRights.row(rowSelector).data(), false);
};

PropertyCtrl.editRight = function (rowSelector) {
    PropertyCtrl.openRight(PropertyCtrl.tableRights.row(rowSelector).data(), true);
};

PropertyCtrl.newRight = function () {
    var rightTypeCode = $("#cbxRightTypes").val();
    if (isNull(rightTypeCode)) {
        return;
    }
    $("#dialogAddRight").modal('hide');
    var right = new PropertyDao.Right();
    right.rightTypeCode = rightTypeCode;
    right.statusCode = Global.STATUS.pending;
    PropertyCtrl.openRight(right, true);
};

PropertyCtrl.openRight = function (right, forEdit) {
    PropertyCtrl.setTile(right);
    var allowRightholdersEditing = false;

    // Customize toolbar
    if (forEdit) {
        $("#btnSaveRight").show();
        if (PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.FirstRegistration ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Registration ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Rectify ||
                PropertyCtrl.appType.transactionTypeCode === RefDataDao.TRANSACTION_TYPE_CODES.Transfer) {
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
        PropertyCtrl.LegalEntities.setLegalEntities(rightholders);
    }

    loadingRight += 1;
    PropertyCtrl.Persons = new Controls.Persons(
            "personRightholders", "divRightholderPerson",
            {persons: rightholders, editable: allowRightholdersEditing, app: PropertyCtrl.app, isOwnership: isOwnership}
    );
    PropertyCtrl.Persons.init(function () {
        loadingRight -= 1;
        showRight();
    });

    loadingRight += 1;
    PropertyCtrl.RightDocs = new Controls.Documents(
            "rightDocs", "divRightDocs",
            {editable: forEdit, documents: makeObjectsList(right.documents, "document"), app: PropertyCtrl.app}
    );
    PropertyCtrl.RightDocs.init(function () {
        loadingRight -= 1;
        showRight();
    });

    loadingRight += 1;
    PropertyCtrl.Pois = new Controls.Pois("rightPois", "divPois", {pois: right.pois, editable: allowRightholdersEditing});
    PropertyCtrl.Pois.init(function () {
        loadingRight -= 1;
        showRight();
    });
    
    showRight = function () {
        if (loadingRight > 0) {
            return;
        }

        PropertyCtrl.showProp(false);
        if (forEdit) {
            $("#rightDiv .glyphicon-required").show();
        } else {
            $("#rightDiv .glyphicon-required").hide();
        }
    };
    
    showRight();
};

PropertyCtrl.showProp = function (show) {
    if (show) {
        $("#rightDiv").hide();
        $("#propDiv").show();
        PropertyCtrl.setTile();
    } else {
        $("#propDiv").hide();
        $("#rightDiv").show();
    }
};

PropertyCtrl.openAddRight = function () {
    // If multiple rights can be added, show popup window
    var rightTypes = PropertyCtrl.getAllowedRightTypes();
    //if (rightTypes.length > 0) {
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
                                    <button type="button" class="btn btn-primary" onclick="PropertyCtrl.newRight()" data-i18n="gen-select"></button> \
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
    //}
};

PropertyCtrl.save = function () {
    if (PropertyCtrl.mapControl !== null) {
        PropertyCtrl.mapControl.saveParcels(function () {
            showNotification($.i18n("parcel-saved"));
        });
    }
};
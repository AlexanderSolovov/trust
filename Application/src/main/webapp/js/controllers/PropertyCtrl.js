/**
 * Contains methods to manage map page. 
 * Requires SearchDao.js, Map.js, Global.js, URLS.js, PropertyDao.js, ApplicationDao.js
 */

var PropertyCtrl = PropertyCtrl || {};
PropertyCtrl.app = null;
PropertyCtrl.prop = null;
PropertyCtrl.parcel = null;
PropertyCtrl.rightId = null;
PropertyCtrl.tableRights;
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

    var showProp = function () {
        if (loadingProcesses > 0) {
            return;
        }
        PropertyCtrl.setTile();

        // Customize toolbars
        $("#rightToolbar").hide();
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

        if (!isNull(PropertyCtrl.app)) {
            $("#btnSave").show();
            $("#btnSave").on("click", PropertyCtrl.save);
            $("#btnBack").show();
            $("#btnBack").on("click", PropertyCtrl.backToApplication);
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
        $("#propDiv").i18n();

        // Show div
        $("#propDiv").show();
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

    // Status
    loadingProcesses += 1;
    RefDataDao.getRecord(RefDataDao.REF_DATA_TYPES.RegStatus.type, PropertyCtrl.prop.statusCode, function (status) {
        if (isNull(status)) {
            showErrorMessage($.i18n("err-prop-status-not-found"));
            return;
        }
        $("#lblPropStatus").text(status.val);
        loadingProcesses -= 1;
        showProp();
    });

    // App to create
    var link = "<a href='{0}' target='_blank'>#{1}</a>";
    if (PropertyCtrl.prop.applicationId === PropertyCtrl.app.id) {
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

    // Rights
    var cols = [
        {data: "rightTypeCode", title: $.i18n("gen-type")},
        {data: "regDate", title: $.i18n("prop-reg-date")},
        {data: "folioNumber", title: $.i18n("right-folio-number")},
        {data: "rightholders", title: $.i18n("right-rightholders")},
        {data: "statusCode", title: $.i18n("gen-status")}
    ];
    
    var colsHistoric = [
        {data: "rightTypeCode", title: $.i18n("gen-type")},
        {data: "regDate", title: $.i18n("prop-reg-date")},
        {data: "terminationDate", title: $.i18n("prop-termination-date")},
        {data: "folioNumber", title: $.i18n("right-folio-number")},
        {data: "rightholders", title: $.i18n("right-rightholders")}
    ];

    PropertyCtrl.tableRights = $("#tableActivePendingRights").DataTable({
        data: PropertyCtrl.getRightsByStatus([Global.STATUS.active, Global.STATUS.pending]),
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

PropertyCtrl.setTile = function () {
    var propNumber;

    if (isNull(PropertyCtrl.prop.propNumber)) {
        propNumber = "#" + $.i18n("gen-new");
    } else {
        propNumber = "#" + PropertyCtrl.prop.propNumber;
    }
    document.title = document.title + " " + $.i18n("prop-prop") + " " + propNumber;
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

PropertyCtrl.save = function () {
    if (PropertyCtrl.mapControl !== null) {
        PropertyCtrl.mapControl.saveParcels(function () {
            showNotification($.i18n("parcel-saved"));
        });
    }
};
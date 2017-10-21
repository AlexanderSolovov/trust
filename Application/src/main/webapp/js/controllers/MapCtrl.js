/**
 * Contains methods to manage map page. 
 * Requires SearchDao.js, Map.js, Global.js, URLS.js, PropertyDao.js
 */

var MapCtrl = MapCtrl || {};
MapCtrl.application = null;
MapCtrl.mapControl = null;
MapCtrl.parcelId = null;
MapCtrl.MESSAGES = {
    saved: "saved"
};

// Load application information and show it
$(document).ready(function () {
    // Show notification if any
    if (!isNull(getUrlParam("msg"))) {
        var messageCode = getUrlParam("msg");
        if (messageCode === MapCtrl.MESSAGES.saved) {
            showNotification($.i18n("parcel-saved"));
        }
    }

    var appId = getUrlParam("app");
    MapCtrl.parcelId = getUrlParam("parcelId");
    
    // Hide toolbar by default
    $("#mapToolbar").hide();

    if (!isNullOrEmpty(appId)) {
        // Check user rights for managing parcels
        if (!Global.USER_PERMISSIONS.canManageParcels) {
            showErrorMessage($.i18n("err-forbidden"));
            return;
        }

        // Get application
        SearchDao.searchAppById(appId, function (result) {
            if (isNull(result)) {
                showErrorMessage($.i18n("err-app-not-found"));
                return;
            }

            MapCtrl.application = result;

            // Add application information into the header
            $("#lblApp").text(String.format($.i18n("app-header-info"), result.appNumber, result.appType));

            // Customize and show toolbar
            $("#btnBack").on("click", MapCtrl.back);
            $("#btnSave").on("click", MapCtrl.save);
            $("#mapToolbar").show();

            MapCtrl.postLoad();
        }, function () {
            showErrorMessage($.i18n("err-app-not-found"));
        });
    } else {
        MapCtrl.postLoad();
    }

});

MapCtrl.postLoad = function () {
    MapCtrl.mapControl = new Controls.Map("mainMap", "pnlMap", {application: MapCtrl.application, parcelId: MapCtrl.parcelId});
    MapCtrl.mapControl.init();

    // Localize
    $("#mapDiv").i18n();

    // Show div
    $("#mapDiv").show();
};

MapCtrl.back = function () {
    window.location.replace(String.format(URLS.VIEW_APPLICATION, MapCtrl.application.id));
};

MapCtrl.save = function () {
    if (MapCtrl.mapControl !== null) {
        MapCtrl.mapControl.saveParcels(function (){
            showNotification($.i18n("parcel-saved"));
        });
    }
};
/**
 * Contains methods to manage Dashboard page. 
 * Requires Global.js, SearchDao.js
 */
var DashboardCtrl = DashboardCtrl || {};

// Load Dashboard
$(document).ready(function () {
    // Load my applications
    var onAssign = function () {
        showNotification($.i18n("app-apps-assigned"));
        search();
    };

    var myApps = new Controls.Applications("ctrlMyApps", "pnlMyApps",
            {
                allowSearch: true,
                allowSelection: Global.USER_PERMISSIONS.canAssign,
                allowAssign: Global.USER_PERMISSIONS.canAssign,
                onAssign: onAssign
            });
    myApps.init();

    // Load all other pending applications
    var pendingApps = new Controls.Applications("ctrlPendingApps", "pnlPendingApps",
            {
                allowSearch: true,
                allowSelection: Global.USER_PERMISSIONS.canReAssign,
                allowAssign: Global.USER_PERMISSIONS.canReAssign,
                onAssign: onAssign
            });
    pendingApps.init();
    
    var search = function () {
        SearchDao.searchMyApps(function (apps) {
            myApps.setApplications(apps);
        });
        SearchDao.searchPendingApps(function (apps) {
            pendingApps.setApplications(apps);
        });
    };
    
    search();

    // Localize
    $("#dashboardDiv").i18n();

    // Show panel
    $("#dashboardDiv").show();

    // Adjust columns
    myApps.adjustColumns();
});

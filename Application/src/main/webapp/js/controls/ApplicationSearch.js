/* 
 * Applications control.
 * Requires Global.js, DataTablesUtility.js, SearchDao.js,  ApplicationDao.js, 
 * Applications.js, ApplicationAssign.js, RefDataDao.js
 */
var Controls = Controls || {};

Controls.ApplicationSearch = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var onSelect = isNull(options.onSelect) ? null : options.onSelect;
    var applicationsControl = null;

    var controlVarId = "__control_applications_search_" + controlId;

    // Assign control variable
    window[controlVarId] = this;

    this.init = function () {
        var loadingCounter = 2;
        var enableSearchButton = function () {
            if (loadingCounter > 0) {
                return;
            }
            $("#" + controlVarId + "_btnSearch").prop('disabled', false);
        };

        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlApplicationSearch.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));

            // Disable search button before loading dropdown lists
            $("#" + controlVarId + "_btnSearch").prop('disabled', true);

            bindDateFields();

            // Add applications control
            applicationsControl = new Controls.Applications(controlVarId + "ctrlApps", controlVarId + "_pnlApplications",
                    {
                        allowSearch: false,
                        allowSelection: false,
                        allowAssign: false,
                        onSelect: onSelect
                    });
            applicationsControl.init();

            // Localize
            $("#" + targetElementId).i18n();

            // load app types
            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.AppType.type, function (list) {
                // Add dummy
                if (list !== null) {
                    var dummy = new RefDataDao.RefData();
                    list.unshift(dummy);
                }
                populateSelectList(list, controlVarId + "_cbxAppTypes");
            }, null, function () {
                loadingCounter -= 1;
                enableSearchButton();
            }, true, true);

            // Load app statuses
            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.AppStatus.type, function (list) {
                // Add dummy
                if (list !== null) {
                    var dummy = new RefDataDao.RefData();
                    list.unshift(dummy);
                }
                populateSelectList(list, controlVarId + "_cbxStatuses");
            }, null, function () {
                loadingCounter -= 1;
                enableSearchButton();
            }, true, true);
        });
    };

    this.search = function () {
        var params = new SearchDao.AppSearchParams();
        params.number = $("#" + controlVarId + "_txtNumber").val();
        params.typeCode = $("#" + controlVarId + "_cbxAppTypes").val();
        params.applicantName = $("#" + controlVarId + "_txtApplicantName").val();
        params.applicantIdNumber = $("#" + controlVarId + "_txtApplicantIdNumber").val();
        params.statusCode = $("#" + controlVarId + "_cbxStatuses").val();
        params.ccroNumber = $("#" + controlVarId + "_txtCcroNumber").val();
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDateFrom").val())) {
            params.lodgemenetDateFrom = dateFormat($("#" + controlVarId + "_txtDateFrom").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDateTo").val())) {
            params.lodgemenetDateTo = dateFormat($("#" + controlVarId + "_txtDateTo").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }

        // Hide any warnings
        $("#" + controlVarId + "_pnlSearchWarning").hide();
        $("#" + controlVarId + "_lblWarningMessage").text("");

        SearchDao.searchApplications(params, function (searchResults) {
            if (isNull(searchResults) || searchResults.length < 1) {
                $("#" + controlVarId + "_lblWarningMessage").text($.i18n("search-nothing-found"));
                $("#" + controlVarId + "_pnlSearchWarning").show();
            } else if (searchResults.length > 1000) {
                $("#" + controlVarId + "_lblWarningMessage").text(String.format($.i18n("search-found-too-many"), "1000"));
                $("#" + controlVarId + "_pnlSearchWarning").show();
            }
            
            // Search results
            if (!isNull(searchResults) && searchResults.length > 0) {
                $("#" + controlVarId + "_lblSearchCount").text(String.format("({0})", searchResults.length));
            } else {
                $("#" + controlVarId + "_lblSearchCount").text("(0)");
            }
            
            if (applicationsControl !== null) {
                applicationsControl.setApplications(searchResults);
            }
        });
    };
};
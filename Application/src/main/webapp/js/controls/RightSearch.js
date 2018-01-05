/* 
 * Property rights search control.
 * Requires Global.js, DataTablesUtility.js, Rights.js, SearchDao.js, RefDataDao.js
 */
var Controls = Controls || {};

Controls.RightSearch = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var height = options.height ? options.height : 0;
    var onSelect = isNull(options.onSelect) ? null : options.onSelect;
    var rightsControl = null;

    var controlVarId = "__control_rights_search_" + controlId;

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
        $.get(Global.APP_ROOT + '/js/templates/ControlRightSearch.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));

            // Disable search button before loading dropdown lists
            $("#" + controlVarId + "_btnSearch").prop('disabled', true);

            // Add applications control
            rightsControl = new Controls.Rights(controlVarId + "ctrlRights", controlVarId + "_pnlRights",
                    {
                        height: height,
                        allowSearch: false,
                        allowAssign: false,
                        onSelect: onSelect
                    });
            rightsControl.init();

            // Localize
            $("#" + targetElementId).i18n();

            // load app types
            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.RightType.type, function (list) {
                // Add dummy
                if (list !== null) {
                    var dummy = new RefDataDao.RefData();
                    list.unshift(dummy);
                }
                populateSelectList(list, controlVarId + "_cbxRightTypes");
            }, null, function () {
                loadingCounter -= 1;
                enableSearchButton();
            }, true, true);

            // Load right statuses
            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.RegStatus.type, function (list) {
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
        var params = new SearchDao.RightSearchParams();
        params.propNumber = $("#" + controlVarId + "_txtPropNumber").val();
        params.rightTypeCode = $("#" + controlVarId + "_cbxRightTypes").val();
        params.rightholderName = $("#" + controlVarId + "_txtRightholderName").val();
        params.rightholderIdNumber = $("#" + controlVarId + "_txtRightholderIdNumber").val();
        params.statusCode = $("#" + controlVarId + "_cbxStatuses").val();
        params.fileNumber = $("#" + controlVarId + "_txtFileNumber").val();
        params.ukaNumber = $("#" + controlVarId + "_txtUka").val();

        // Hide any warnings
        $("#" + controlVarId + "_pnlSearchWarning").hide();
        $("#" + controlVarId + "_lblWarningMessage").text("");

        SearchDao.searchRights(params, function (searchResults) {
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
            
            if (rightsControl !== null) {
                rightsControl.setRights(searchResults);
            }
        });
    };
};
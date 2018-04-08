/* 
 * Reference data table control. Used to manage districts records.
 * Requires RefDataDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Districts = function (controlId, targetElementId) {
    validateControl(controlId, targetElementId);
    
    var table;
    var controlVarId = "__control_districts_" + controlId;
    var regions = null;
    var activeRegions = null;
    window[controlVarId] = this;
    
    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlDistricts.html', function (tmpl) {
            // Render template
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(
                    template({
                        id: controlVarId,
                        languageRows: splitArrayInRows(Global.LANGUAGES, 2),
                        multipleLanguages: Global.LANGUAGES.length > 1
                    }));

            // Localize
            $("#" + targetElementId).i18n();

            // Load ref data
            RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.Region.type, function (list) {
                regions = list;
                if(regions !== null && regions.length > 0){
                    activeRegions = RefDataDao.filterActiveRecords(regions);
                }
                RefDataDao.getAllRecordsUnlocalized(RefDataDao.REF_DATA_TYPES.District.type, function (data) {
                    loadTable(data);
                });
            }, null, null, true, true);
        });
    };

    var loadTable = function loadTable(data) {
        table = $('#' + controlVarId).DataTable({
            data: data,
            "dom": '<"tableToolbar">frt',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "code", title: $.i18n("ref-code")},
                {data: "val", title: $.i18n("gen-val")},
                {title: $.i18n("ref-region")},
                {data: "active", title: $.i18n("gen-active")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, full, meta) {
                        return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteRecord($(this).parents('tr'));return false;") +
                                String.format(DataTablesUtility.getEditLink(), controlVarId + ".showDialog($(this).parents('tr'));return false;") +
                                " " + data;
                    }
                },
                {
                    targets: 1,
                    "render": function (data, type, full, meta) {
                        return getLocalizedValue(data);
                    }
                },
                {
                    targets: 2,
                    "render": function (data, type, row, meta) {
                        if(regions !== null && regions.length > 0){
                            for(var i = 0; i < regions.length; i++){
                                if(regions[i].code === row.regionCode){
                                    return regions[i].val;
                                }
                            }
                        }
                        return "";
                    }
                },
                {
                    targets: 3,
                    width: "80px",
                    "render": function (data, type, full, meta) {
                        if (data) {
                            return '<i class="glyphicon glyphicon-ok"></i>';
                        }
                        return '<i class="glyphicon glyphicon-minus"></i>';
                    }
                }
            ]
        });
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showDialog(null);return false;"));
    };

    var selectedRow = null;

    this.showDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
            fillForm(null);
        } else {
            selectedRow = table.row(rowSelector);
            fillForm(selectedRow.data());
        }
    };

    var fillForm = function (data) {
        if (data === null) {
            data = {};
        }
        $("#" + controlVarId + "_txtCode").val(data.code);
        $("#" + controlVarId + "_txtCode").prop('disabled', (typeof data.code !== 'undefined' && data.code !== '') ? true : false);
        populateSelectList(activeRegions, controlVarId + "_cbxRegions", true);
        $("#" + controlVarId + "_cbxRegions").val(data.regionCode);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_hVersion").val(data.version);
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            $("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val(getLocalizedValueByLang(data.val, Global.LANGUAGES[i].code));
        }
    };

    this.saveRecord = function () {
        // Prepare JSON
        if(isNullOrEmpty($("#" + controlVarId + "_cbxRegions").val())){
            alertWarningMessage($.i18n("err-ref-no-region"));
            return;
        }
        
        var district = new RefDataDao.District();
        district.code = $("#" + controlVarId + "_txtCode").val().trim();
        district.regionCode = $("#" + controlVarId + "_cbxRegions").val();
        district.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        district.version = $("#" + controlVarId + "_hVersion").val();

        // Loop thought languages and make unlocalized string for val attribute
        var localizedValues = [];
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            localizedValues.push($("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val());
        }

        district.val = makeUnlocalizedValue(localizedValues).trim();

        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (district.code.toLowerCase() === table.data()[i].code.toLowerCase()) {
                    alertErrorMessage($.i18n("err-code-exists"));
                    return;
                }
            }
        }

        RefDataDao.saveRecord(RefDataDao.REF_DATA_TYPES.District.type, district, function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(district).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(district).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteRecord = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                RefDataDao.deleteRecord(RefDataDao.REF_DATA_TYPES.District.type, table.row(rowSelector).data().code, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                });
            });
        }
    };
};

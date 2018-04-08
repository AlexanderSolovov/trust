/* 
 * Reference data table control. Used to manage villages records.
 * Requires RefDataDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Villages = function (controlId, targetElementId) {
    validateControl(controlId, targetElementId);

    var table;
    var controlVarId = "__control_villages_" + controlId;
    var regions = null;
    var districts = null;
    var districts = null;
    var activeRegions = null;
    window[controlVarId] = this;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlVillages.html', function (tmpl) {
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

            // Load regions and districts
            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.Region.type, function (list) {
                regions = list;
                if (regions !== null && regions.length > 0) {
                    activeRegions = RefDataDao.filterActiveRecords(regions);
                }

                if (!isNull(regions)) {
                    populateSelectList(regions, controlVarId + "_cbxRegions");
                }

                // Districts
                RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.District.type, function (list) {
                    districts = list;
                    if (districts !== null && districts.length > 0) {
                        districts = RefDataDao.filterActiveRecords(districts);
                    }

                    loadTable(null);
                }, null, null, true, true);
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
                {title: $.i18n("ref-district")},
                {data: "address", title: $.i18n("village-address")},
                {data: "chairman", title: $.i18n("village-chairman")},
                {data: "executiveOfficer", title: $.i18n("village-eo")},
                {data: "active", title: $.i18n("gen-active")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "150px",
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
                        if (districts !== null && districts.length > 0) {
                            for (var i = 0; i < districts.length; i++) {
                                if (districts[i].code === row.districtCode) {
                                    return districts[i].val;
                                }
                            }
                        }
                        return "";
                    }
                },
                {
                    targets: 6,
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

    this.fillDistricts = function () {
        $("#" + controlVarId + "_cbxDistricts").empty();
        var regionCode = $("#" + controlVarId + "_cbxRegions").val();

        if (!isNullOrEmpty(regionCode))
            RefDataDao.getDistrictsByRegion(regionCode, function (districts) {
                if (!isNull(districts)) {
                    populateSelectList(districts, controlVarId + "_cbxDistricts");
                }
            });
    };

    this.fillVillages = function () {
        var districtCode = $("#" + controlVarId + "_cbxDistricts").val();
        table.clear();

        if (!isNullOrEmpty(districtCode)) {
            RefDataDao.getVillagesByDistrict(districtCode, function (villages) {
                if (isNull(villages)) {
                    villages = [];
                }
                table.rows.add(villages);
                table.draw();
            });
        } else {
            table.draw();
        }
    };

    this.fillVillageDistricts = function () {
        $("#" + controlVarId + "_cbxVillageDistricts").empty();
        var regionCode = $("#" + controlVarId + "_cbxVillageRegions").val();

        if (!isNullOrEmpty(regionCode)) {
            if (!isNullOrEmpty(districts) && districts.length > 0) {
                var villageDistricts = [];
                for (var j = 0; j < districts.length; j++) {
                    if (districts[j].regionCode === regionCode) {
                        villageDistricts.push(districts[j]);
                    }
                }
                populateSelectList(villageDistricts, controlVarId + "_cbxVillageDistricts", true);
            }
        }
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
        $("#" + controlVarId + "_cbxVillageRegions").empty();
        $("#" + controlVarId + "_cbxVillageDistricts").empty();
        populateSelectList(activeRegions, controlVarId + "_cbxVillageRegions", true);

        // Fill in regions and district
        var districtCode = data.districtCode;
        if (isNullOrEmpty(districtCode)) {
            districtCode = $("#" + controlVarId + "_cbxDistricts").val();
        }
        
        if (!isNullOrEmpty(districtCode)) {
            for (var i = 0; i < districts.length; i++) {
                if (districts[i].code === districtCode) {
                    $("#" + controlVarId + "_cbxVillageRegions").val(districts[i].regionCode);
                    // Populate districts
                    var villageDistricts = [];
                    for (var j = 0; j < districts.length; j++) {
                        if (districts[j].regionCode === districts[i].regionCode) {
                            villageDistricts.push(districts[j]);
                        }
                    }
                    populateSelectList(villageDistricts, controlVarId + "_cbxVillageDistricts", true);
                    $("#" + controlVarId + "_cbxVillageDistricts").val(districtCode);
                    break;
                }
            }
        }
        
        $("#" + controlVarId + "_txtChairman").val(data.chairman);
        $("#" + controlVarId + "_txtEo").val(data.executiveOfficer);
        $("#" + controlVarId + "_txtAddress").val(data.address);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_hVersion").val(data.version);
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            $("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val(getLocalizedValueByLang(data.val, Global.LANGUAGES[i].code));
        }
    };

    this.saveRecord = function () {
        // Prepare JSON
        if (isNullOrEmpty($("#" + controlVarId + "_cbxVillageRegions").val())) {
            alertWarningMessage($.i18n("err-ref-no-region"));
            return;
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxVillageDistricts").val())) {
            alertWarningMessage($.i18n("err-ref-no-district"));
            return;
        }

        var village = new RefDataDao.Village();
        village.code = $("#" + controlVarId + "_txtCode").val().trim();
        village.districtCode = $("#" + controlVarId + "_cbxVillageDistricts").val();
        village.chairman = $("#" + controlVarId + "_txtChairman").val().trim();
        village.executiveOfficer = $("#" + controlVarId + "_txtEo").val().trim();
        village.address = $("#" + controlVarId + "_txtAddress").val().trim();
        village.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        village.version = $("#" + controlVarId + "_hVersion").val();

        // Loop thought languages and make unlocalized string for val attribute
        var localizedValues = [];
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            localizedValues.push($("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val());
        }

        village.val = makeUnlocalizedValue(localizedValues).trim();

        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (village.code.toLowerCase() === table.data()[i].code.toLowerCase()) {
                    alertErrorMessage($.i18n("err-code-exists"));
                    return;
                }
            }
        }

        RefDataDao.saveRecord(RefDataDao.REF_DATA_TYPES.Village.type, village, function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(village).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(village).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteRecord = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                RefDataDao.deleteRecord(RefDataDao.REF_DATA_TYPES.Village.type, table.row(rowSelector).data().code, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                });
            });
        }
    };
};

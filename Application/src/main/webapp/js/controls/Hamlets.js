/* 
 * Reference data table control. Used to manage hamlets records.
 * Requires RefDataDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Hamlets = function (controlId, targetElementId) {
    validateControl(controlId, targetElementId);

    var table;
    var controlVarId = "__control_hamlets_" + controlId;
    var regions = null;
    var districts = null;
    var activeDistricts = null;
    var activeRegions = null;
    var villages = null;
    window[controlVarId] = this;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlHamlets.html', function (tmpl) {
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
                        activeDistricts = RefDataDao.filterActiveRecords(districts);
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
                {title: $.i18n("ref-village")},
                {data: "abbr", title: $.i18n("hamlet-abbr")},
                {data: "leader", title: $.i18n("hamlet-leader")},
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
                        if (villages !== null && villages.length > 0) {
                            for (var i = 0; i < villages.length; i++) {
                                if (villages[i].code === row.villageCode) {
                                    return villages[i].val;
                                }
                            }
                        }
                        return "";
                    }
                },
                {
                    targets: 5,
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
        $("#" + controlVarId + "_cbxVillages").empty();
        var districtCode = $("#" + controlVarId + "_cbxDistricts").val();

        if (!isNullOrEmpty(districtCode)) {
            RefDataDao.getVillagesByDistrict(districtCode, function (list) {
                villages = list;
                if (!isNull(villages)) {
                    populateSelectList(villages, controlVarId + "_cbxVillages");
                }
            });
        }
    };

    this.fillHamlets = function () {
        var villageCode = $("#" + controlVarId + "_cbxVillages").val();
        table.clear();

        if (!isNullOrEmpty(villageCode)) {
            RefDataDao.getHamletsByVillage(villageCode, function (hamlets) {
                if (isNull(hamlets)) {
                    hamlets = [];
                }
                table.rows.add(hamlets);
                table.draw();
            });
        } else {
            table.draw();
        }
    };

    this.fillHamletDistricts = function () {
        $("#" + controlVarId + "_cbxHamletDistricts").empty();
        var regionCode = $("#" + controlVarId + "_cbxHamletRegions").val();

        if (!isNullOrEmpty(regionCode)) {
            if (!isNullOrEmpty(activeDistricts) && activeDistricts.length > 0) {
                var hamletDistricts = [];
                for (var j = 0; j < activeDistricts.length; j++) {
                    if (activeDistricts[j].regionCode === regionCode) {
                        hamletDistricts.push(activeDistricts[j]);
                    }
                }
                populateSelectList(hamletDistricts, controlVarId + "_cbxHamletDistricts", true);
            }
        }
    };

    this.fillHamletVillages = function () {
        $("#" + controlVarId + "_cbxHamletVillages").empty();
        var districtCode = $("#" + controlVarId + "_cbxHamletDistricts").val();

        if (!isNullOrEmpty(districtCode)) {
            RefDataDao.getVillagesByDistrict(districtCode, function (list) {
                villages = list;
                if (!isNull(villages)) {
                    populateSelectList(villages, controlVarId + "_cbxHamletVillages");
                }
            });
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
        $("#" + controlVarId + "_cbxHamleteRegions").empty();
        $("#" + controlVarId + "_cbxHamletDistricts").empty();
        $("#" + controlVarId + "_cbxHamletVillages").empty();
        populateSelectList(activeRegions, controlVarId + "_cbxHamletRegions", true);

        // Fill in regions, district and village
        var villageCode = data.villageCode;
        if (isNullOrEmpty(villageCode)) {
            villageCode = $("#" + controlVarId + "_cbxVillages").val();
        }

        if (!isNullOrEmpty(villageCode)) {
            // Fill in villages
            populateSelectList(villages, controlVarId + "_cbxHamletVillages");
            $("#" + controlVarId + "_cbxHamletVillages").val(villageCode);
            var hamletVillage = null;
            
            for (var i = 0; i < villages.length; i++) {
                if(villages[i].code === villageCode){
                    hamletVillage = villages[i];
                    break;
                }
            }

            for (var i = 0; i < districts.length; i++) {
                if (districts[i].code === hamletVillage.districtCode) {
                    $("#" + controlVarId + "_cbxHamletRegions").val(districts[i].regionCode);
                    // Populate districts
                    var hamletDistricts = [];
                    for (var j = 0; j < districts.length; j++) {
                        if (districts[j].regionCode === districts[i].regionCode) {
                            hamletDistricts.push(districts[j]);
                        }
                    }
                    populateSelectList(hamletDistricts, controlVarId + "_cbxHamletDistricts", true);
                    $("#" + controlVarId + "_cbxHamletDistricts").val(hamletVillage.districtCode);
                    break;
                }
            }
        }

        $("#" + controlVarId + "_txtAbbr").val(data.abbr);
        $("#" + controlVarId + "_txtLeaderName").val(data.leader);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_hVersion").val(data.version);
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            $("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val(getLocalizedValueByLang(data.val, Global.LANGUAGES[i].code));
        }
    };

    this.saveRecord = function () {
        // Prepare JSON
        if (isNullOrEmpty($("#" + controlVarId + "_cbxHamletVillages").val())) {
            alertWarningMessage($.i18n("err-ref-no-village"));
            return;
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtAbbr").val())) {
            alertWarningMessage($.i18n("err-ref-abbr-empty"));
            return;
        }

        var hamlet = new RefDataDao.Hamlet();
        hamlet.code = $("#" + controlVarId + "_txtCode").val().trim();
        hamlet.villageCode = $("#" + controlVarId + "_cbxHamletVillages").val();
        hamlet.abbr = $("#" + controlVarId + "_txtAbbr").val().trim();
        hamlet.leader = $("#" + controlVarId + "_txtLeaderName").val().trim();
        hamlet.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        hamlet.version = $("#" + controlVarId + "_hVersion").val();

        // Loop thought languages and make unlocalized string for val attribute
        var localizedValues = [];
        for (i = 0; i < Global.LANGUAGES.length; i++) {
            localizedValues.push($("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val());
        }

        hamlet.val = makeUnlocalizedValue(localizedValues).trim();

        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (hamlet.code.toLowerCase() === table.data()[i].code.toLowerCase()) {
                    alertErrorMessage($.i18n("err-code-exists"));
                    return;
                }
            }
        }

        RefDataDao.saveRecord(RefDataDao.REF_DATA_TYPES.Hamlet.type, hamlet, function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(hamlet).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(hamlet).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteRecord = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                RefDataDao.deleteRecord(RefDataDao.REF_DATA_TYPES.Hamlet.type, table.row(rowSelector).data().code, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                });
            });
        }
    };
};

/* 
 * Legal entities control.
 * Requires RefDataDao.js, PartyDao.js, LegalEntity.js, LegalEntityView.js, LegalEntitySearch.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.LegalEntities = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    var filterParties = function (list) {
        if (isNull(list)) {
            return [];
        }
        var result = [];
        for (var i = 0; i < list.length; i++) {
            if (!isNull(list[i].isPrivate) && !list[i].isPrivate) {
                result.push(list[i]);
            }
        }
        return result;
    };

    options = options ? options : {};
    var legalEntities = filterParties(options.legalEntities);
    var editable = isNull(options.editable) ? true : options.editable;
    var that = this;
    var table;
    var controlVarId = "__control_legal_entities_" + controlId;
    var loaded = false;
    var leTypes;
    var leControl = null;
    var leViewControl = null;
    var leSearchControl = null;

    this.init = function (onInit) {
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.LegalEntityType.type, function (list) {
            leTypes = list;
        }, null, function () {
            // Load control template
            $.get(Global.APP_ROOT + '/js/templates/ControlLegalEntities.html', function (tmpl) {
                var template = Handlebars.compile(tmpl);
                $('#' + targetElementId).html(template({id: controlVarId}));
                // Localize
                $("#" + targetElementId).i18n();

                // Assign control variable
                eval(controlVarId + " = that;");

                loadTable(legalEntities);
                loaded = true;

                if (isFunction(onInit)) {
                    onInit();
                }
            });
        }, true, true);
    };

    var loadTable = function loadTable(data) {
        table = $('#' + controlVarId).DataTable({
            data: data,
            "paging": false,
            "info": false,
            "sort": false,
            "searching": false,
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "name1", title: $.i18n("gen-name")},
                {data: "entityTypeCode", title: $.i18n("gen-type")},
                {data: "idNumber", title: $.i18n("le-reg-num")},
                {data: "establishmentDate", title: $.i18n("le-reg-date")},
                {data: "mobileNumber", title: $.i18n("person-mobile-num")},
                {data: "address", title: $.i18n("gen-address")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "200px",
                    "render": function (data, type, row, meta) {
                        if (editable) {
                            return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteLegalEntity($(this).parents('tr'));return false;") +
                                    String.format(DataTablesUtility.getEditLink(), controlVarId + ".showLegalEntityDialog($(this).parents('tr'));return false;") +
                                    " " + data;
                        } else {
                            return String.format(DataTablesUtility.getViewLink(), controlVarId + ".showLegalEntityDialog($(this).parents('tr'));return false;", data);
                        }
                    }
                },
                {
                    targets: 1,
                    "render": function (data, type, row, meta) {
                        if (!isNullOrEmpty(data)) {
                            var leType = RefDataDao.getRefDataByCode(leTypes, data);
                            if (!isNullOrEmpty(leType)) {
                                return leType.val;
                            } else {
                                return "";
                            }
                        }
                        return "";
                    }
                },
                {
                    targets: 3,
                    "render": function (data, type, row, meta) {
                        if (type === "display") {
                            return dateFormat(data, dateFormat.masks.dateTime);
                        }
                        return data;
                    }
                }
            ]
        });

        var copyFromApp = "";
        if (!isNull(options.app)) {
            copyFromApp = "&nbsp;&nbsp;" + String.format(DataTablesUtility.getCopyFromAppLink(), controlVarId + ".copyFromApp();return false;");
        }
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(
                String.format(DataTablesUtility.getAddLink(), controlVarId + ".showLegalEntityDialog(null);return false;")
                + "&nbsp;&nbsp;" +
                String.format(DataTablesUtility.getSearchLink(), controlVarId + ".showSearchDialog();return false;") +
                copyFromApp
                );

        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }
    };

    this.getLegalEntities = function () {
        var records = [];
        table.rows().data().each(function (d) {
            records.push(d);
        });
        if (records.length < 1) {
            return null;
        } else {
            return records;
        }
    };

    this.setLegalEntities = function (list) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }
        table.clear();
        legalEntities = filterParties(list);
        table.rows.add(legalEntities);
        table.draw();
    };

    this.setEditable = function (allowEdit) {
        editable = allowEdit;
        if (allowEdit) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }
        table.draw();
    };

    var selectedRow = null;

    this.copyFromApp = function () {
        if (!isNull(options.app.applicants)) {
            for (var i = 0; i < options.app.applicants.length; i++) {
                var party = options.app.applicants[i].party;

                if (!party.isPrivate) {
                    // Check if it's already exists
                    var found = false;
                    table.rows().data().each(function (p) {
                        if (String.empty(p.id) === party.id) {
                            found = true;
                        }
                    });

                    if (!found) {
                        highlight(table.row.add(party).draw().node());
                    }
                }
            }
        }
    };

    this.showLegalEntityDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
        } else {
            selectedRow = table.row(rowSelector);
        }

        var legalEntity = isNull(selectedRow) ? null : selectedRow.data();

        if ((isNull(legalEntity) || legalEntity.editable) && editable) {
            $("#" + controlVarId + "_leview").hide();
            $("#" + controlVarId + "_le").show();
            if (isNull(leControl)) {
                leControl = new Controls.LegalEntity(controlVarId + "_le", controlVarId + "_le", legalEntity);
                leControl.init();
            } else {
                leControl.setLegalEntity(legalEntity);
            }
            $("#" + controlVarId + "_btnSaveLe").show();
        } else if (!legalEntity.editable || !editable) {
            $("#" + controlVarId + "_leview").show();
            $("#" + controlVarId + "_le").hide();
            if (isNull(leViewControl)) {
                leViewControl = new Controls.LegalEntityView(controlVarId + "_leview", controlVarId + "_leview", legalEntity);
                leViewControl.init();
            } else {
                leViewControl.setLegalEntity(legalEntity);
            }
            $("#" + controlVarId + "_btnSaveLe").hide();
        }
    };

    var selectLegalEntity = function (leSearchResult) {
        if (!isNull(leSearchResult)) {
            var found = false;
            // Check first if legal entity already in the list
            table.rows().data().each(function (l) {
                if (String.empty(l.id) === leSearchResult.id) {
                    found = true;
                }
            });
            if (found) {
                // Close window and do nothing
                $("#" + controlVarId + "_SearchDialog").modal('hide');
                return;
            }

            // Get full record of legal entity and add to the list
            $("#" + controlVarId + "_SearchDialog").modal('hide');
            PartyDao.getParty(leSearchResult.id, function (l) {
                var row = table.row.add(l).draw().node();
                highlight(row);
            });
        }
    };

    this.showSearchDialog = function () {
        $("#" + controlVarId + "_SearchDialog").modal('show');
        if (isNull(leSearchControl)) {
            leSearchControl = new Controls.LegalEntitySearch(controlVarId + "_leSearch", controlVarId + "_leSearch", {selectFunc: selectLegalEntity, height: 300});
            leSearchControl.init();
            $("#" + controlVarId + "_SearchDialog").on('shown.bs.modal', function () {
                $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
            });
        }
    };

    this.saveLegalEntity = function () {
        if (isNull(leControl) || !leControl.isLoaded()) {
            return;
        }
        // Add/update person
        var legalEntity = leControl.getLegalEntity(true);
        if (!isNull(legalEntity)) {
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(legalEntity).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(legalEntity).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        }
    };

    this.deleteLegalEntity = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                // Remove from table 
                table.row(rowSelector).remove().draw();
            });
        }
    };
};

/* 
 * Persons control.
 * Requires PropertyDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Pois = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var pois = options.pois;
    var editable = isNull(options.editable) ? true : options.editable;
    var table;
    var loaded = false;
    var controlVarId = "__control_pois_" + controlId;
    var poi;

    // Assign control variable
    window[controlVarId] = this;

    this.init = function (onInit) {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlPois.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));
            // Localize
            $("#" + targetElementId).i18n();

            loadTable(pois);
            bindDateFields();
            loaded = true;

            if (isFunction(onInit)) {
                onInit();
            }
        });
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
                {data: "fullName", title: $.i18n("gen-name")},
                {data: "dob", title: $.i18n("person-dob")},
                {data: "description", title: $.i18n("gen-description")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, row, meta) {
                        if (editable) {
                            return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deletePoi($(this).parents('tr'));return false;") +
                                    String.format(DataTablesUtility.getEditLink(), controlVarId + ".showPoiDialog($(this).parents('tr'));return false;") +
                                    " " + getFullName(row);
                        } else {
                            return String.format(DataTablesUtility.getViewLink(), controlVarId + ".showPoiDialog($(this).parents('tr'));return false;", getFullName(row));
                        }
                    }
                },
                {
                    targets: 1,
                    "render": function (data, type, row, meta) {
                        if (!isNull(data)) {
                            return dateFormat(data);
                        }
                    }
                }
            ]
        });

        $("#" + controlVarId + "_wrapper div.tableToolbar")
                .html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showPoiDialog(null);return false;"));

        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }
    };

    this.getPois = function () {
        var records = [];
        table.rows().data().each(function (d) {
            records.push(d);
        });
        if (records.length < 1) {
            return [];
        } else {
            return records;
        }
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

    this.setPois = function (list) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }
        table.clear();
        pois = list ? list : [];
        table.rows.add(pois);
        table.draw();
    };

    var selectedRow = null;

    this.showPoiDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
        } else {
            selectedRow = table.row(rowSelector);
        }

        poi = isNull(selectedRow) ? new PropertyDao.Poi() : selectedRow.data();

        if (editable) {
            $("#" + controlVarId + "_lblFirstName").hide();
            $("#" + controlVarId + "_lblMiddleName").hide();
            $("#" + controlVarId + "_lblLastName").hide();
            $("#" + controlVarId + "_lblDob").hide();
            $("#" + controlVarId + "_lblDescription").hide();

            $("#" + controlVarId + "_btnSavePoi").show();
            $("#" + controlVarId + "_Dialog .glyphicon-required").show();
            $("#" + controlVarId + "_txtFirstName").show();
            $("#" + controlVarId + "_txtMiddleName").show();
            $("#" + controlVarId + "_txtLastName").show();
            $("#" + controlVarId + "_groupDob").show();
            $("#" + controlVarId + "_txtDescription").show();

            $("#" + controlVarId + "_txtFirstName").val(poi.firstName);
            $("#" + controlVarId + "_txtMiddleName").val(poi.middleName);
            $("#" + controlVarId + "_txtLastName").val(poi.lastName);
            $("#" + controlVarId + "_txtDescription").val(poi.description);

            if (isNull(poi.dob)) {
                $("#" + controlVarId + "_txtDob").val("");
            } else {
                $("#" + controlVarId + "_txtDob").val(dateFormat(poi.dob));
            }
        } else {
            $("#" + controlVarId + "_lblFirstName").show();
            $("#" + controlVarId + "_lblMiddleName").show();
            $("#" + controlVarId + "_lblLastName").show();
            $("#" + controlVarId + "_lblDob").show();
            $("#" + controlVarId + "_lblDescription").show();

            $("#" + controlVarId + "_btnSavePoi").hide();
            $("#" + controlVarId + "_Dialog .glyphicon-required").hide();
            $("#" + controlVarId + "_txtFirstName").hide();
            $("#" + controlVarId + "_txtMiddleName").hide();
            $("#" + controlVarId + "_txtLastName").hide();
            $("#" + controlVarId + "_groupDob").hide();
            $("#" + controlVarId + "_txtDescription").hide();

            $("#" + controlVarId + "_lblFirstName").text(poi.firstName);
            $("#" + controlVarId + "_lblMiddleName").text(poi.middleName);
            $("#" + controlVarId + "_lblLastName").text(poi.lastName);
            $("#" + controlVarId + "_lblDescription").text(poi.description);

            if (isNull(poi.dob)) {
                $("#" + controlVarId + "_lblDob").text("");
            } else {
                $("#" + controlVarId + "_lblDob").text(dateFormat(poi.dob));
            }
        }
    };

    this.savePoi = function () {
        // Validate
        var errors = [];

        if (isNullOrEmpty($("#" + controlVarId + "_txtFirstName").val())) {
            errors.push($.i18n("err-person-firstname-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtLastName").val())) {
            errors.push($.i18n("err-person-lastname-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtDob").val())) {
            errors.push($.i18n("err-person-dob-empty"));
        }
        if (errors.length > 0) {
            alertErrorMessages(errors);
            return;
        }

        // Add/update poi
        var result = new PropertyDao.Poi();
        if (!isNull(poi)) {
            result.id = poi.id;
            result.version = poi.version;
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_txtDob").val())) {
            result.dob = dateFormat($("#" + controlVarId + "_txtDob").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtFirstName").val())) {
            result.firstName = $("#" + controlVarId + "_txtFirstName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtMiddleName").val())) {
            result.middleName = $("#" + controlVarId + "_txtMiddleName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLastName").val())) {
            result.lastName = $("#" + controlVarId + "_txtLastName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDescription").val())) {
            result.description = $("#" + controlVarId + "_txtDescription").val();
        }

        $("#" + controlVarId + "_Dialog").modal('hide');
        var currentRow;

        // if selected row is null, then add row
        if (selectedRow === null) {
            currentRow = table.row.add(result).draw().node();
        } else {
            // Update row
            currentRow = selectedRow.data(result).draw().node();
        }
        // Animate changed/added row
        highlight(currentRow);
    };

    this.deletePoi = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                // Remove from table 
                table.row(rowSelector).remove().draw();
            });
        }
    };

    var getFullName = function (p) {
        var fullName = String.empty(p.firstName);
        if (!isNullOrEmpty(p.middleName)) {
            if (!isNullOrEmpty(fullName)) {
                fullName = fullName + " " + p.middleName;
            } else {
                fullName = p.middleName;
            }
        }
        if (!isNullOrEmpty(p.lastName)) {
            if (!isNullOrEmpty(fullName)) {
                fullName = fullName + " " + p.lastName;
            } else {
                fullName = p.lastName;
            }
        }
        return fullName;
    };
};

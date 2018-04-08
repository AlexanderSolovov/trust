/* 
 * Reference data table control. Used to manage map layer records.
 * Requires RefDataDao.js, DatatablesUtility.js, SystemDao.js
 */
var Controls = Controls || {};

Controls.MapLayers = function (controlId, targetElementId) {
    validateControl(controlId, targetElementId);

    var table;
    var optionsTable;
    var controlVarId = "__control_maplayers_" + controlId;
    var layerTypes = null;
    var editedLayer = null;
    var editedOption = null;
    window[controlVarId] = this;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/MapLayers.html', function (tmpl) {
            // Render template
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(
                    template({
                        id: controlVarId
                    }));

            // Localize
            $("#" + targetElementId).i18n();

            // Load layers
            RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.LayerType.type, function (list) {
                layerTypes = list;
                populateSelectList(layerTypes, controlVarId + "_cbxLayerTypes", true);
                SystemDao.getAllMapLayers(function (data) {
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
                {data: "name", title: $.i18n("gen-name")},
                {data: "title", title: $.i18n("gen-title")},
                {title: $.i18n("ref-layer-type")},
                {data: "url", title: $.i18n("gen-url")},
                {data: "order", title: $.i18n("gen-order")},
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
                    targets: 2,
                    "render": function (data, type, row, meta) {
                        if (layerTypes !== null && layerTypes.length > 0) {
                            for (var i = 0; i < layerTypes.length; i++) {
                                if (layerTypes[i].code === row.typeCode) {
                                    return layerTypes[i].val;
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

        // Options
        optionsTable = $('#' + controlVarId + "_tableOptions").DataTable({
            data: null,
            "dom": '<"tableToolbar">frt',
            language: DataTablesUtility.getLanguage(),
            "sort": false,
            "searching": false,
            columns: [
                {data: "name", title: $.i18n("gen-name")},
                {data: "val", title: $.i18n("gen-val")},
                {data: "forServer", title: $.i18n("map-control-for-server")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "200px",
                    "render": function (data, type, full, meta) {
                        return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteOption($(this).parents('tr'));return false;") +
                                String.format(DataTablesUtility.getEditLink(), controlVarId + ".showOptionDialog($(this).parents('tr'));return false;") +
                                " " + data;
                    }
                },
                {
                    targets: 2,
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
        $("#" + controlVarId + "_addOption").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showOptionDialog(null);return false;"));
    };

    var selectedRow = null;
    var selectedOptionRow = null;

    this.showDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
            editedLayer = null;
            fillForm(null);
        } else {
            selectedRow = table.row(rowSelector);
            editedLayer = selectedRow.data();
            fillForm(editedLayer);
        }
    };

    var fillForm = function (data) {
        if (data === null) {
            data = {};
        }
        $("#" + controlVarId + "_txtName").val(data.name);
        $("#" + controlVarId + "_txtTitle").val(data.title);
        $("#" + controlVarId + "_cbxLayerTypes").val(data.typeCode);
        $("#" + controlVarId + "_txtUrl").val(data.url);
        $("#" + controlVarId + "_txtOrder").val(data.order);
        $("#" + controlVarId + "_txtVersionNum").val(data.versionNum);
        $("#" + controlVarId + "_txtImageFormat").val(data.imageFormat);
        $("#" + controlVarId + "_txtUserName").val(data.userName);
        $("#" + controlVarId + "_txtPassword").val(data.passwd);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);

        optionsTable.clear();
        optionsTable.rows.add(data.options);
        optionsTable.draw();
    };

    this.saveRecord = function () {
        // Prepare JSON
        if (isNullOrEmpty($("#" + controlVarId + "_txtName").val())) {
            alertWarningMessage($.i18n("err-name-empty"));
            return;
        }

        if (isNullOrEmpty($("#" + controlVarId + "_txtTitle").val())) {
            alertWarningMessage($.i18n("err-title-empty"));
            return;
        }

        if (isNullOrEmpty($("#" + controlVarId + "_cbxLayerTypes").val())) {
            alertWarningMessage($.i18n("err-layer-type-empty"));
            return;
        }

        if (isNullOrEmpty($("#" + controlVarId + "_txtUrl").val())) {
            alertWarningMessage($.i18n("err-url-empty"));
            return;
        }

        if (isNullOrEmpty($("#" + controlVarId + "_txtOrder").val())) {
            alertWarningMessage($.i18n("err-order-empty"));
            return;
        }

        var mapLayer = new SystemDao.MapLayer();
        if (editedLayer !== null) {
            mapLayer.id = editedLayer.id;
            mapLayer.version = editedLayer.version;
        }
        mapLayer.name = $("#" + controlVarId + "_txtName").val().trim();
        mapLayer.title = $("#" + controlVarId + "_txtTitle").val();
        mapLayer.typeCode = $("#" + controlVarId + "_cbxLayerTypes").val();
        mapLayer.url = $("#" + controlVarId + "_txtUrl").val();
        mapLayer.order = $("#" + controlVarId + "_txtOrder").val();
        mapLayer.url = $("#" + controlVarId + "_txtUrl").val();
        mapLayer.versionNum = $("#" + controlVarId + "_txtVersionNum").val();
        mapLayer.imageFormat = $("#" + controlVarId + "_txtImageFormat").val();
        mapLayer.userName = $("#" + controlVarId + "_txtUserName").val();
        mapLayer.passwd = $("#" + controlVarId + "_txtPassword").val();
        mapLayer.active = $("#" + controlVarId + "_cbxActive").prop("checked");

        var options = [];
        optionsTable.rows().data().each(function (d) {
            options.push(d);
        });

        if (options.length > 0) {
            mapLayer.options = options;
        }

        SystemDao.saveMapLayer(mapLayer, function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(mapLayer).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(mapLayer).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteRecord = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                var layerId = table.row(rowSelector).data().id;
                
                if (isNullOrEmpty(layerId)) {
                    // Delete only in the table
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                } else {
                    SystemDao.deleteMapLayer(layerId, function () {
                        // Remove from table 
                        table.row(rowSelector).remove().draw();
                        showNotification($.i18n("gen-delete-success"));
                    });
                }
            });
        }
    };

    this.showOptionDialog = function (rowSelector) {
        $("#" + controlVarId + "_OptionDialog").modal('show');
        if (isNull(rowSelector)) {
            selectedOptionRow = null;
            editedOption = null;
            fillOptionForm(null);
        } else {
            selectedOptionRow = optionsTable.row(rowSelector);
            editedOption = selectedOptionRow.data();
            fillOptionForm(editedOption);
        }
    };

    var fillOptionForm = function (data) {
        if (data === null) {
            data = {};
        }
        $("#" + controlVarId + "_txtOptionName").val(data.name);
        $("#" + controlVarId + "_txtOptionValue").val(data.val);
        $("#" + controlVarId + "_cbxForServer").prop("checked", (typeof data.forServer !== 'undefined') ? data.forServer : true);
    };

    this.saveOption = function () {
        if (isNullOrEmpty($("#" + controlVarId + "_txtOptionName").val())) {
            alertWarningMessage($.i18n("err-name-empty"));
            return;
        }

        if (isNullOrEmpty($("#" + controlVarId + "_txtOptionValue").val())) {
            alertWarningMessage($.i18n("err-value-empty"));
            return;
        }

        var option = new RefDataDao.District();
        option.name = $("#" + controlVarId + "_txtOptionName").val().trim();
        option.val = $("#" + controlVarId + "_txtOptionValue").val();
        option.forServer = $("#" + controlVarId + "_cbxForServer").prop("checked");

        if (editedOption !== null) {
            option.id = editedOption.id;
            option.version = editedOption.version;
        }

        if (selectedOptionRow === null && optionsTable.data()) {
            for (i = 0; i < optionsTable.data().length; i++) {
                if (option.name.toLowerCase() === optionsTable.data()[i].name.toLowerCase()) {
                    alertErrorMessage($.i18n("err-option-exists"));
                    return;
                }
            }
        }

        // Close dialog
        $("#" + controlVarId + "_OptionDialog").modal('hide');
        var currentRow;

        // if selected row is null, then add row
        if (selectedOptionRow === null) {
            currentRow = optionsTable.row.add(option).draw().node();
        } else {
            // Update row
            currentRow = selectedOptionRow.data(option).draw().node();
        }
        // Animate changed/added row
        highlight(currentRow);
    };

    this.deleteOption = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                optionsTable.row(rowSelector).remove().draw();
            });
        }
    };
};

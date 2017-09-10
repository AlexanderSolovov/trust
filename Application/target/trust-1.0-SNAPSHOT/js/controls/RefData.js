/* 
 * Reference data table control. Used to manage common refence data records.
 * Requires RefDataDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.RefData = function (controlId, targetElementId, refDataType) {
    validateControl(controlId, targetElementId);

    var that = this;
    var table;
    var controlVarId = "__control_refdata_" + controlId;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlRefData.html', function (tmpl) {
            // Render template
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(
                    template({
                        id: controlVarId,
                        dialogLabel: $.i18n(refDataType.labelSingle),
                        languageRows: splitArrayInRows(Global.LANGUAGES, 2),
                        multipleLanguages: Global.LANGUAGES.length > 1
                    }));

            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");

            // Load ref data
            RefDataDao.getAllRecordsUnlocalized(refDataType.type, function (data) {
                loadTable(data);
            });
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
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_hVersion").val(data.version);
        for(i=0; i<Global.LANGUAGES.length; i++){
            $("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val(getLocalizedValueByLang(data.val, Global.LANGUAGES[i].code));
        }
    };

    this.saveRecord = function () {
        // Prepare JSON
        var refData = new RefDataDao.RefData();
        refData.code = $("#" + controlVarId + "_txtCode").val().trim();
        refData.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        refData.version = $("#" + controlVarId + "_hVersion").val();
        
        // Loop thought languages and make unlocalized string for val attribute
        var localizedValues = [];
        for(i=0; i<Global.LANGUAGES.length; i++){
            localizedValues.push($("#" + controlVarId + "_txtValue_" + Global.LANGUAGES[i].code).val());
        }
        
        refData.val = makeUnlocalizedValue(localizedValues).trim();
        
        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (refData.code.toLowerCase() === table.data()[i].code.toLowerCase()) {
                    alertErrorMessage($.i18n("err-code-exists"));
                    return;
                }
            }
        }

        RefDataDao.saveRecord(refDataType.type, refData, function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(refData).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(refData).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteRecord = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                RefDataDao.deleteRecord(refDataType.type, table.row(rowSelector).data().code, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                });
            });
        }
    };
};

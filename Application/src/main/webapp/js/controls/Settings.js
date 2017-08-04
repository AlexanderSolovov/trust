/* 
 * System settings control.
 * Requires SystemDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Settings = function (controlId, targetElementId) {
    if (controlId === null || typeof controlId === 'undefined') {
        throw "Control id is not provdided";
    }
    if (targetElementId === null || typeof targetElementId === 'undefined') {
        throw "Target element id is not provdided";
    }

    var that = this;
    var table;
    var controlVarId = "__control_settings_" + controlId;

    this.init = function () {
        $.get(Global.APP_ROOT + '/js/templates/ControlSettings.html', function (tmpl) {
            // Load control template
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));

            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");
            //window['chat_' + id] = that;

            // Load settings
            SystemDao.getSettings(function (data) {
                loadTable(data);
            });
        });
    };

    var loadTable = function loadTable(data) {
        table = $('#' + controlVarId).DataTable({
            data: data,
            "paging": false,
            "info": false,
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "id", title: $.i18n("gen-name")},
                {data: "val", title: $.i18n("gen-val")},
                {data: "description", title: $.i18n("gen-description")},
                {data: "active", title: $.i18n("gen-active")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, full, meta) {
                        return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteSetting($(this).parents('tr'));return false;") +
                                String.format(DataTablesUtility.getEditLink(), controlVarId + ".showSettingDialog($(this).parents('tr'));return false;") +
                                " " + data;
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
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showSettingDialog(null);return false;"));
    };

    var selectedRow = null;

    this.showSettingDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (rowSelector === null || typeof rowSelector === 'undefined') {
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
        $("#" + controlVarId + "_txtName").val(data.id);
        $("#" + controlVarId + "_txtName").prop('disabled', (typeof data.id !== 'undefined' && data.id !== '') ? true : false);
        $("#" + controlVarId + "_txtValue").val(data.val);
        $("#" + controlVarId + "_txtDescription").val(data.description);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_hVersion").val(data.version);
    };

    this.saveSetting = function () {
        // Prepare JSON
        var setting = new SystemDao.Setting();
        setting.id = $("#" + controlVarId + "_txtName").val();
        setting.val = $("#" + controlVarId + "_txtValue").val();
        setting.description = $("#" + controlVarId + "_txtDescription").val();
        setting.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        setting.version = $("#" + controlVarId + "_hVersion").val();
        
        if (selectedRow === null && table.data()) {
            for (i=0; i<table.data().length; i++){
                if(setting.id.toLowerCase() === table.data()[i].id.toLowerCase()){
                    alertErrorMessage($.i18n("err-name-exists"));
                    return;
                }
            }
        }

        SystemDao.saveSetting(setting, function (response) {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(response).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(response).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        });
    };

    this.deleteSetting = function (rowSelector) {
        if (rowSelector !== null || typeof rowSelector !== 'undefined') {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                SystemDao.deleteSetting(table.row(rowSelector).data().id, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                });
            });
        }
    };
};

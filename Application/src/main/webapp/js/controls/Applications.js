/* 
 * Applications control.
 * Requires Global.js, DataTablesUtility.js, SearchDao.js,  ApplicationDao.js, ApplicationAssign.js
 */
var Controls = Controls || {};

Controls.Applications = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var allowSearch = isNull(options.allowSearch) ? true : options.allowSearch;
    var allowAssign = isNull(options.allowAssign) ? true : options.allowAssign;
    var allowSelection = isNull(options.allowSelection) ? true : options.allowSelection;
    var onSelect = isNull(options.onSelect) ? null : options.onSelect;
    var onAssign = isNull(options.onAssign) ? null : options.onAssign;
    var applications = options.applications ? options.applications : [];
    var table;
    var selectedApps = [];
    var controlVarId = "__control_applications_" + controlId;
    var appAsignControl = null;

    // Assign control variable
    window[controlVarId] = this;

    this.init = function () {
        var html = '<table id="' + controlVarId + '" class="table table-striped table-bordered table-hover" style="width:100%"></table>';
        if (allowAssign) {
            html += '<div id="' + controlVarId + '_pnlAssign">';
        }

        $('#' + targetElementId).html(html);

        // Init application assignment component
        if (allowAssign) {
            appAsignControl = new Controls.ApplicationAssign(controlVarId + "_ctrlAssign", controlVarId + '_pnlAssign',
                    {
                        onAssign: function () {
                            if (isFunction(onAssign)) {
                                onAssign();
                            }
                        }
                    });
            appAsignControl.init();
        }
        loadTable(applications);
    };

    var loadTable = function loadTable(data) {
        var cols = [
            {data: "appNumber", title: $.i18n("search-number")},
            {data: "appType", title: $.i18n("gen-type")},
            {data: "lodgementDate", title: $.i18n("app-lodgement-date")},
            {data: "applicantData", title: $.i18n("app-applicants")},
            {data: "ccros", title: $.i18n("app-ccros")},
            {data: "assigneeName", title: $.i18n("app-assignee")},
            {data: "appStatus", title: $.i18n("gen-status")}
        ];

        var colsDef = [
            {
                targets: allowSelection ? 1 : 0,
                "render": function (data, type, row, meta) {
                    if (isFunction(onSelect)) {
                        return String.format(DataTablesUtility.getViewLink(), controlVarId + ".selectApplication($(this).parents('tr'));return false;", data);
                    } else {
                        return '<a href="' + String.format(ApplicationDao.URL_VIEW_APPLICATION, row.id) + '">' + data + '</a>';
                    }
                }
            },
            {
                targets: allowSelection ? 3 : 2,
                width: "125px",
                "render": function (data, type, row, meta) {
                    return dateFormat(data, dateFormat.masks.dateTime);
                }
            },
            {
                targets: allowSelection ? 4 : 3,
                "render": function (data, type, row, meta) {
                    return replaceNewLineWithBr(data);
                }
            }
        ];

        if (allowSelection) {
            cols.unshift({title: " ", "orderable": false});
            colsDef.unshift({
                targets: 0,
                "orderable": false,
                width: "20px",
                "render": function (data, type, row, meta) {
                    return '<input type="checkbox" onclick="' + controlVarId + '.selectDeselectRow(this, \'' + row.id + '\')">';
                }
            });
        }

        table = $("#" + controlVarId).DataTable({
            data: data,
            "paging": false,
            "info": false,
            "sort": true,
            "searching": allowSearch,
            "scrollY": null,
            "scrollCollapse": true,
            "deferRender": true,
            "order": allowSelection ? [[3, 'desc']] : [[2, 'desc']],
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: cols,
            columnDefs: colsDef
        });

        if (allowAssign) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").html(
                    '<button type="button" id="' + controlVarId + '_btnAssign" class="btn btn-default" disabled \
                        onclick="' + controlVarId + '.showAssignDialog();return false;">' +
                    '<i class="glyphicon glyphicon-share-alt"></i> <span data-i18n="app-assign"></span>' +
                    '</button>'
                    );
        }
        new $.fn.dataTable.FixedHeader(table, {
            // options
        });
    };

    this.selectDeselectRow = function (checkbox, id) {
        // Search for id
        var found = false;
        for (var i = 0; i < selectedApps.length; i++) {
            if (selectedApps[i] === id) {
                // Remove is unchecked
                if (!checkbox.checked) {
                    selectedApps.splice(i, 1);
                }
                // Set found to true 
                found = true;
                break;
            }
        }

        if (checkbox.checked) {
            if (!found) {
                selectedApps.push(id);
            }
        }
        // Select / deselect row
        $(checkbox).closest("tr").toggleClass('selectedRow');
        // Enable/disable bottons
        if (allowAssign) {
            $("#" + controlVarId + "_btnAssign").prop('disabled', selectedApps.length < 1);
        }
    };

    this.showAssignDialog = function () {
        if (appAsignControl !== null && selectedApps.length > 0) {
            appAsignControl.showAssignDialog(selectedApps);
        }
    };

    this.setApplications = function (list) {
        table.clear();
        applications = list ? list : [];
        selectedApps = [];
        table.rows.add(applications);
        table.draw();
    };

    this.selectApplication = function (rowSelector) {
        if (isFunction(onSelect)) {
            onSelect(table.row(rowSelector).data());
        }
    };

    this.adjustColumns = function () {
        table.columns.adjust();
    };
};
/* 
 * Property rights control.
 * Requires Global.js, DataTablesUtility.js, URLS.js
 */
var Controls = Controls || {};

Controls.Rights = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var allowSearch = isNull(options.allowSearch) ? true : options.allowSearch;
    var onSelect = isNull(options.onSelect) ? null : options.onSelect;
    var height = options.height ? options.height : 0;
    var rights = options.rights ? options.rights : [];
    var table;
    var controlVarId = "__control_rights_" + controlId;

    // Assign control variable
    window[controlVarId] = this;

    this.init = function () {
        var html = '<table id="' + controlVarId + '" class="table table-striped table-bordered table-hover" style="width:100%"></table>';
        $('#' + targetElementId).html(html);

        loadTable(rights);
    };

    var loadTable = function loadTable(data) {
        table = $("#" + controlVarId).DataTable({
            data: data,
            "paging": false,
            "info": false,
            "sort": true,
            "searching": allowSearch,
            "scrollY": (height > 0 ? height + "px" : null),
            "order": [[0, 'asc']],
            "scrollCollapse": true,
            "deferRender": true,
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "propNumber", title: $.i18n("search-ccro-number")},
                {data: "fileNumber", title: $.i18n("prop-file-number")},
                {data: "uka", title: $.i18n("parcel-uka")},
                {data: "rightType", title: $.i18n("ref-right-type")},
                {data: "rightholderData", title: $.i18n("right-rightholders")},
                {data: "status", title: $.i18n("gen-status")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    "render": function (data, type, row, meta) {
                        if (type === "display") {
                            if (isFunction(onSelect)) {
                                return String.format(DataTablesUtility.getViewLink(), controlVarId + ".selectProperty($(this).parents('tr'));return false;", data);
                            } else {
                                return String.format(DataTablesUtility.getViewLinkCurrentWindow(), String.format(URLS.VIEW_PROPERTY, row.propId), data);
                            }
                        }
                        return data;
                    }
                },
                {
                    targets: 2,
                    "render": function (data, type, row, meta) {
                        if (isFunction(onSelect)) {
                            return data;
                        } else {
                            return String.format(DataTablesUtility.getViewLinkCurrentWindow(), String.format(URLS.VIEW_MAP_WITH_PARCEL, row.parcelId), data);
                        }
                    }
                },
                {
                    targets: 3,
                    "render": function (data, type, row, meta) {
                        if (isFunction(onSelect)) {
                            return data;
                        } else {
                            return String.format(DataTablesUtility.getViewLinkCurrentWindow(), String.format(URLS.VIEW_PROPERTY_BY_RIGHT, row.id), data);
                        }
                    }
                },
                {
                    targets: 4,
                    "render": function (data, type, row, meta) {
                        return replaceNewLineWithBr(data);
                    }
                }
            ]
        });
        new $.fn.dataTable.FixedHeader(table, {
            // options
        });
    };

    this.setRights = function (list) {
        table.clear();
        rights = list ? list : [];
        table.rows.add(rights);
        table.draw();
    };

    this.selectProperty = function (rowSelector) {
        if (isFunction(onSelect)) {
            onSelect(table.row(rowSelector).data());
        }
    };

    this.adjustColumns = function () {
        table.columns.adjust();
    };
};
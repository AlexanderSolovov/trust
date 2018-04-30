/* 
 * Legal entity search control.
 * Requires PartyDao.js, SearchDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.LegalEntitySearch = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var selectFunc = options.selectFunc ? options.selectFunc : null;
    var height = options.height ? options.height : 0;
    var that = this;
    var table;
    var controlVarId = "__control_legal_entity_search_" + controlId;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlLegalEntitySearch.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));
            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");

            loadTable();
        });
    };

    var loadTable = function loadTable(data) {
        table = $('#' + controlVarId).DataTable({
            data: data,
            "paging": false,
            "info": false,
            "sort": false,
            "searching": false,
            "scrollY": (height > 0 ? height + "px" : null),
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "name", title: $.i18n("gen-name")},
                {data: "entityType", title: $.i18n("gen-type")},
                {data: "regNumber", title: $.i18n("le-reg-num")},
                {data: "establishmentDate", title: $.i18n("le-reg-date")},
                {data: "mobileNumber", title: $.i18n("person-mobile-num")},
                //{data: "address", title: $.i18n("gen-address")}
                {data: "ccros", title: $.i18n("app-ccros")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "200px",
                    "render": function (data, type, row, meta) {
                        if (isFunction(selectFunc)) {
                            if (String.empty(row.statusCode) === Global.STATUS.active) {
                                return String.format(DataTablesUtility.getViewLink(), controlVarId + ".selectLegalEntity($(this).parents('tr'));return false;", data);
                            } else {
                                return data;
                            }
                        } else {
                            return '<a href="' + Global.APP_ROOT + '/Party/ViewParty.jsp?id=' + row.id + '">' + data + '</a>';
                        }
                    }
                },
                {
                    targets: 3,
                    "render": function (data, type, row, meta) {
                        return dateFormat(data);
                    }
                }
            ]
        });
    };

    this.search = function () {
        SearchDao.searchLegalEntity($("#" + controlVarId + "_name").val(),
                $("#" + controlVarId + "_regNumber").val(),
                $("#" + controlVarId + "_ccro").val(),
                function (list) {
                    table.clear();
                    if (isNull(list)) {
                        list = [];
                    }
                    table.rows.add(list);
                    table.draw();
                });
    };

    this.selectLegalEntity = function (rowSelector) {
        if (isFunction(selectFunc)) {
            selectFunc(table.row(rowSelector).data());
        }
    };
};

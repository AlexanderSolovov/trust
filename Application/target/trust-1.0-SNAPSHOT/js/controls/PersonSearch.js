/* 
 * Person search control.
 * Requires SearchDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.PersonSearch = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var selectFunc = options.selectFunc ? options.selectFunc : null;
    var height = options.height ? options.height : 0;
    var that = this;
    var table;
    var controlVarId = "__control_person_search_" + controlId;

    this.init = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlPersonSearch.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));
            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");

            loadTable();
        });
    };

    var loadTable = function loadTable() {
        table = $('#' + controlVarId).DataTable({
            data: [],
            "paging": false,
            "info": false,
            "sort": true,
            "searching": false,
            "scrollY": (height > 0 ? height + "px" : null),
            "scrollCollapse": true,
            "dom": 'frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "name", title: $.i18n("gen-name")},
                {title: $.i18n("person-id-data")},
                {data: "dob", title: $.i18n("person-dob")},
                {data: "mobileNumber", title: $.i18n("person-mobile-num")},
                {data: "address", title: $.i18n("gen-address")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "200px",
                    "render": function (data, type, row, meta) {
                        if (isFunction(selectFunc)) {
                            if (String.empty(row.statusCode) === Global.STATUS.active) {
                                return String.format(DataTablesUtility.getViewLink(), controlVarId + ".selectPerson($(this).parents('tr'));return false;", data);
                            } else {
                                return data;
                            }
                        } else {
                            return '<a href="' + Global.APP_ROOT + '/Party/ViewParty.jsp?id=' + row.id + '">' + data + '</a>';
                        }
                    }
                },
                {
                    targets: 1,
                    "render": function (data, type, row, meta) {
                        var idData = String.empty(row.idNumber);
                        if (!isNullOrEmpty(row.idType)) {
                            if (!isNullOrEmpty(idData)) {
                                idData = idData + "<br>";
                            }
                            idData = idData + "(" + row.idType + ")";
                        }
                        return idData;
                    }
                },
                {
                    targets: 2,
                    "render": function (data, type, row, meta) {
                        return dateFormat(data);
                    }
                }
            ]
        });
    };

    this.search = function () {
        SearchDao.searchPerson($("#" + controlVarId + "_name").val(), $("#" + controlVarId + "_idNumber").val(), function (list) {
            table.clear();
            if (isNull(list)) {
                list = [];
            }
            table.rows.add(list);
            table.draw();
        });
    };

    this.selectPerson = function (rowSelector) {
        if (isFunction(selectFunc)) {
            selectFunc(table.row(rowSelector).data());
        }
    };
};

/* 
 * Persons control.
 * Requires RefDataDao.js, PartyDao.js, Person.js, PersonView.js, PersonSearch.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Persons = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var persons = options.persons ? options.persons : [];
    var editable = isNull(options.editable) ? true : options.editable;
    var that = this;
    var table;
    var controlVarId = "__control_persons_" + controlId;
    var loaded = false;
    var idTypes;
    var personControl = null;
    var personViewControl = null;
    var personSearchControl = null;

    this.init = function (onInit) {
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.IdType.type, function (list) {
            idTypes = list;
        }, null, function () {
            // Load control template
            $.get(Global.APP_ROOT + '/js/templates/ControlPersons.html', function (tmpl) {
                var template = Handlebars.compile(tmpl);
                $('#' + targetElementId).html(template({id: controlVarId}));
                // Localize
                $("#" + targetElementId).i18n();

                // Assign control variable
                eval(controlVarId + " = that;");

                loadTable(persons);
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
                {data: "fullName", title: $.i18n("gen-name")},
                {title: $.i18n("person-id-data")},
                {data: "dob", title: $.i18n("person-dob")},
                {data: "mobileNumber", title: $.i18n("person-mobile-num")},
                {data: "address", title: $.i18n("gen-address")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, row, meta) {
                        if (editable) {
                            return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deletePerson($(this).parents('tr'));return false;") +
                                    String.format(DataTablesUtility.getEditLink(), controlVarId + ".showPersonDialog($(this).parents('tr'));return false;") +
                                    " " + data;
                        } else {
                            return String.format(DataTablesUtility.getViewLink(), controlVarId + ".showPersonDialog($(this).parents('tr'));return false;", data);
                        }
                    }
                },
                {
                    targets: 1,
                    width: "120px",
                    "render": function (data, type, row, meta) {
                        var idData = String.empty(row.idNumber);
                        if (!isNullOrEmpty(row.idTypeCode)) {
                            if (!isNullOrEmpty(idData)) {
                                idData = idData + "<br>";
                            }

                            var idType = RefDataDao.getRefDataByCode(idTypes, row.idTypeCode);
                            var idTypeName = "";

                            if (!isNullOrEmpty(idType)) {
                                idTypeName = idType.val;
                            }

                            idData = idData + "(" + idTypeName + ")";
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

        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").html(
                    String.format(DataTablesUtility.getAddLink(), controlVarId + ".showPersonDialog(null);return false;")
                    + "&nbsp;&nbsp;" +
                    String.format(DataTablesUtility.getSearchLink(), controlVarId + ".showSearchDialog();return false;")
                    );
        }
    };

    this.getPersons = function () {
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

    this.setPersons = function (list) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }
        table.clear();
        persons = list ? list : [];
        table.rows.add(persons);
        table.draw();
    };

    var selectedRow = null;

    this.showPersonDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
        } else {
            selectedRow = table.row(rowSelector);
        }

        var person = isNull(selectedRow) ? null : selectedRow.data();

        if (isNull(person) || person.editable) {
            $("#" + controlVarId + "_personview").hide();
            $("#" + controlVarId + "_person").show();
            if (isNull(personControl)) {
                personControl = new Controls.Person(controlVarId + "_person", controlVarId + "_person", person);
                personControl.init();
            } else {
                personControl.setPerson(person);
            }
        } else if (!person.editable) {
            $("#" + controlVarId + "_personview").show();
            $("#" + controlVarId + "_person").hide();
            if (isNull(personViewControl)) {
                personViewControl = new Controls.PersonView(controlVarId + "_personview", controlVarId + "_personview", person);
                personViewControl.init();
            } else {
                personViewControl.setPerson(person);
            }
        }
    };

    var selectPerson = function (personSearchResult) {
        if (!isNull(personSearchResult)) {
            var found = false;
            // Check first if person already in the list
            table.rows().data().each(function (p) {
                if (String.empty(p.id) === personSearchResult.id) {
                    found = true;
                }
            });
            if (found) {
                // Close window and do nothing
                $("#" + controlVarId + "_SearchDialog").modal('hide');
                return;
            }

            // Get full record of person and add to the list
            $("#" + controlVarId + "_SearchDialog").modal('hide');
            PartyDao.getPerson(personSearchResult.id, function (p) {
                var row = table.row.add(p).draw().node();
                highlight(row);
            });
        }
    };

    this.showSearchDialog = function () {
        $("#" + controlVarId + "_SearchDialog").modal('show');
        if (isNull(personSearchControl)) {
            personSearchControl = new Controls.PersonSearch(controlVarId + "_personSearch", controlVarId + "_personSearch", {selectFunc: selectPerson, height: 300});
            personSearchControl.init();
            $("#" + controlVarId + "_SearchDialog").on('shown.bs.modal', function () {
                $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
            });
        }
    };

    this.savePerson = function () {
        if (isNull(personControl) || !personControl.isLoaded()) {
            return;
        }
        // Add/update person
        personControl.getPerson(function (person) {
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(person).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(person).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        }, true);
    };

    this.deletePerson = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                // Remove from table 
                table.row(rowSelector).remove().draw();
            });
        }
    };
};
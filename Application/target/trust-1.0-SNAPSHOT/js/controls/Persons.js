/* 
 * Persons control.
 * Requires RefDataDao.js, PartyDao.js, Person.js, PersonView.js, PersonSearch.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Persons = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    var filterParties = function (list) {
        if (isNull(list)) {
            return [];
        }
        var result = [];
        for (var i = 0; i < list.length; i++) {
            if (!isNull(list[i].isPrivate) && list[i].isPrivate) {
                result.push(list[i]);
            }
        }
        return result;
    };

    options = options ? options : {};
    var persons = filterParties(options.persons);
    var parentPersons = filterParties(options.parentPersons);
    var editable = isNull(options.editable) ? true : options.editable;
    var isOwnership = isNull(options.isOwnership) ? false : options.isOwnership;
    var isChangeOfName = isNull(options.isChangeOfName) ? false : options.isChangeOfName;
    var that = this;
    var table;
    var controlVarId = "__control_persons_" + controlId;
    var loaded = false;
    var idTypes;
    var ownerTypes;
    var personControl = null;
    var personViewControl = null;
    var personSearchControl = null;

    this.init = function (onInit) {
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.IdType.type, function (list) {
            idTypes = list;
        }, null, function () {
            RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.OwnerType.type, function (list) {
                ownerTypes = list;
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
        }, true, true);
    };

    var loadTable = function loadTable(records) {
        table = $('#' + controlVarId).DataTable({
            data: records,
            "paging": false,
            "info": false,
            "sort": false,
            "searching": false,
            "dom": '<"tableToolbar">frtip',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "fullName", title: $.i18n("gen-name")},
                {data: "ownerTypeCode", title: $.i18n("person-role"), visible: isOwnership},
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
                                    String.format(DataTablesUtility.getEditLink(), controlVarId + ".showPersonDialog($(this).parents('tr'), true);return false;") +
                                    " " + data;
                        } else if (isChangeOfName) {
                            // Check list of parent parties to show/hide links. 
                            // If party has same id as parent, it can be changed to new person from application. 
                            // Otherwise, if id is not matching it means person is already changed and revert link should be shown
                            // If at least one party is already changed, remaining parties should staty unchanged
                            var personChangedId = null;

                            for (var i = 0; i < persons.length; i++) {
                                personChangedId = persons[i].id;
                                for (var j = 0; j < parentPersons.length; j++) {
                                    if (parentPersons[j].id === persons[i].id) {
                                        personChangedId = null;
                                        break;
                                    }
                                }
                                if (!isNull(personChangedId)) {
                                    break;
                                }
                            }

                            // There is person who already changed for new owner
                            if (!isNull(personChangedId)) {
                                // If it's current person, return revert link
                                if (row.id === personChangedId) {
                                    return String.format(DataTablesUtility.getRevertLink(), controlVarId + ".revertPerson($(this).parents('tr'));return false;") + "<br> " + data;
                                } else {
                                    // Return only person name
                                    return data;
                                }
                            } else {
                                // Allow changes for all owners
                                return String.format(DataTablesUtility.getChangeLink(), controlVarId + ".changePerson($(this).parents('tr'));return false;") + "<br> " + data;
                            }
                        } else {
                            return String.format(DataTablesUtility.getViewLink(), controlVarId + ".showPersonDialog($(this).parents('tr'), false);return false;", data);
                        }
                    }
                },
                {
                    targets: 1,
                    "render": function (data, type, row, meta) {
                        var ownerType = RefDataDao.getRefDataByCode(ownerTypes, data);
                        if (!isNullOrEmpty(ownerType)) {
                            return ownerType.val;
                        }
                        return "";
                    }
                },
                {
                    targets: 2,
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
                    targets: 3,
                    "render": function (data, type, row, meta) {
                        if (type === "display") {
                            return dateFormat(data, dateFormat.masks.default);
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
                String.format(DataTablesUtility.getAddLink(), controlVarId + ".showPersonDialog(null, true);return false;")
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

    this.reInit = function (options) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }
        persons = filterParties(options.persons);
        parentPersons = filterParties(options.parentPersons);
        if (!isNull(options.editable)) {
            editable = options.editable;
        }
        if (!isNull(options.isOwnership)) {
            isOwnership = options.isOwnership;
        }
        if (!isNull(options.isChangeOfName)) {
            isChangeOfName = options.isChangeOfName;
        }

        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }

        table.destroy();
        $('#' + controlVarId).empty();
        loadTable(persons);
    };

    this.setIsChangeOfName = function (isNameChange) {
        isChangeOfName = isNameChange;
        table.draw();
    };

    var selectedRow = null;

    this.copyFromApp = function () {
        if (!isNull(options.app.applicants)) {
            for (var i = 0; i < options.app.applicants.length; i++) {
                var party = options.app.applicants[i].party;

                if (party.isPrivate) {
                    // Check if it's already exists
                    var found = false;
                    table.rows().data().each(function (p) {
                        if (String.empty(p.id) === party.id) {
                            found = true;
                        }
                    });

                    if (!found) {
                        // Add owner type related fields
                        if (isOwnership) {
                            //party.ownerTypeCode = RefDataDao.OWNER_TYPE_CODES.Owner;
                        }
                        var row = table.row.add(party).node();
                        table.draw();
                        highlight(row);
                    }
                }
            }
        }
    };

    this.changePerson = function (rowSelector) {
        if (!isNull(options.app.applicants)) {
            for (var i = 0; i < options.app.applicants.length; i++) {
                var party = options.app.applicants[i].party;

                if (party.isPrivate) {
                    // Check if it's already exists
                    var found = false;
                    table.rows().data().each(function (p) {
                        if (String.empty(p.id) === party.id) {
                            found = true;
                        }
                    });

                    if (!found) {
                        // Copy role and share from current person to the applicant
                        var personToRemove = table.row(rowSelector).data();
                        party.ownerTypeCode = personToRemove.ownerTypeCode;
                        party.shareSize = personToRemove.shareSize;

                        // Delete old owner
                        for (var i = 0; i < persons.length; i++) {
                            if (persons[i].id === personToRemove.id) {
                                persons.splice(i, 1);
                                break;
                            }
                        }
                        table.row(rowSelector).remove();

                        // Add applicant
                        persons.push(party);
                        var row = table.row.add(party).node();
                        table.draw();
                        highlight(row);
                        break;
                    }
                }
            }
        }
    };

    this.revertPerson = function (rowSelector) {
        if (!isNull(parentPersons)) {
            var selectedPerson = table.row(rowSelector).data();

            for (var j = 0; j < parentPersons.length; j++) {
                var found = false;
                for (var i = 0; i < persons.length; i++) {
                    if (parentPersons[j].id === persons[i].id) {
                        found = true;
                        break;
                    }
                }

                if(!found){
                    // Delete current person
                    for (var i = 0; i < persons.length; i++) {
                        if (persons[i].id === selectedPerson.id) {
                            persons.splice(i, 1);
                            break;
                        }
                    }
                    
                    table.row(rowSelector).remove();

                    // Revert to parent person
                    persons.push(parentPersons[j]);
                    var row = table.row.add(parentPersons[j]).node();
                    table.draw();
                    highlight(row);

                    break;
                }
            }
        }
    };

    this.showPersonDialog = function (rowSelector, forEdit) {
        $("#" + controlVarId + "_Dialog").modal('show');
        if (isNull(rowSelector)) {
            selectedRow = null;
        } else {
            selectedRow = table.row(rowSelector);
        }

        var person = isNull(selectedRow) ? null : selectedRow.data();

        if (forEdit && (person === null || person.editable || isOwnership)) {
            $("#" + controlVarId + "_personview").hide();
            $("#" + controlVarId + "_person").show();
            if (isNull(personControl)) {
                personControl = new Controls.Person(controlVarId + "_person", controlVarId + "_person", {person: person, isOwnership: isOwnership, isChangeOfName: isChangeOfName});
                personControl.init();
            } else {
                personControl.setPerson(person);
            }
            $("#" + controlVarId + "_btnSavePerson").show();
            personControl.selectMainTab();
        } else {
            $("#" + controlVarId + "_personview").show();
            $("#" + controlVarId + "_person").hide();
            if (isNull(personViewControl)) {
                personViewControl = new Controls.PersonView(controlVarId + "_personview", controlVarId + "_personview", {person: person, isOwnership: isOwnership});
                personViewControl.init();
            } else {
                personViewControl.setPerson(person);
            }
            $("#" + controlVarId + "_btnSavePerson").hide();
            personViewControl.selectMainTab();
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
            PartyDao.getParty(personSearchResult.id, function (p) {
                var row = table.row.add(p).node();
                table.draw();
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
                currentRow = table.row.add(person).node();
            } else {
                // Update row
                currentRow = selectedRow.data(person).node();
            }

            table.draw();

            // Animate changed/added row
            highlight(currentRow);
        }, true);
    };

    this.deletePerson = function (rowSelector) {
        if (!isNull(rowSelector)) {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                // Remove from table 
                table.row(rowSelector).remove();
                table.draw();
            });
        }
    };
};

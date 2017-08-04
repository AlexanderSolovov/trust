/* 
 * System settings control.
 * Requires SystemDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Groups = function (controlId, targetElementId) {
    if (controlId === null || typeof controlId === 'undefined') {
        throw "Control id is not provdided";
    }
    if (targetElementId === null || typeof targetElementId === 'undefined') {
        throw "Target element id is not provdided";
    }

    var that = this;
    var table;
    var controlVarId = "__control_groups_" + controlId;
    var roles;

    this.init = function () {
        // Load list of roles first
        SystemDao.getRoles(function (rolesList) {
            roles = rolesList;
        }, null, function () {
            // Load control template
            $.get(Global.APP_ROOT + '/js/templates/ControlGroups.html', function (tmpl) {
                var template = Handlebars.compile(tmpl);
                $('#' + targetElementId).html(template({id: controlVarId, roles: roles}));
                // Localize
                $("#" + targetElementId).i18n();

                // Assign control variable
                eval(controlVarId + " = that;");

                SystemDao.getGroups(function (data) {
                    loadTable(data);
                });
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
                {data: "groupName", title: $.i18n("gen-name")},
                {data: "description", title: $.i18n("gen-description")},
                {data: "roleCodes", title: $.i18n("role-roles")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, full, meta) {
                        return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteGroup($(this).parents('tr'));return false;") +
                                String.format(DataTablesUtility.getEditLink(), controlVarId + ".showGroupDialog($(this).parents('tr'));return false;") +
                                " " + data;
                    }
                },
                {
                    targets: 2,
                    "render": function (data, type, full, meta) {
                        return getRolesList(data);
                    }
                }
            ]
        });
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showGroupDialog(null);return false;"));
    };

    var getRolesList = function (roleCodes) {
        var rolesList = "";
        if (!isNull(roles) && !isNull(roleCodes)) {
            for (i = 0; i < roleCodes.length; i++) {
                for (j = 0; j < roles.length; j++) {
                    if (roles[j].code === roleCodes[i].roleCode) {
                        if (rolesList === "") {
                            rolesList = roles[j].roleName;
                        } else {
                            rolesList = rolesList + ", " + roles[j].roleName;
                        }
                        break;
                    }
                }

            }
        }
        return rolesList;
    };

    var selectedRow = null;

    this.showGroupDialog = function (rowSelector) {
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
        $("#" + controlVarId + "_hGroupId").val(data.id);
        $("#" + controlVarId + "_txtName").val(data.groupName);
        $("#" + controlVarId + "_txtDescription").val(data.description);
        $("#" + controlVarId + "_hVersion").val(data.version);
        if(roles){
            for(i=0; i<roles.length; i++){
                var found = false;
                if(data.roleCodes){
                    for(j=0; j<data.roleCodes.length; j++){
                        if(roles[i].code === data.roleCodes[j].roleCode){
                            found = true;
                            break;
                        }
                    }
                }
                $("#" + controlVarId + "_cbxRole_" + roles[i].code).prop("checked", found);
            }
        }
    };

    this.saveGroup = function () {
        // Prepare JSON
        var group = new SystemDao.Group();
        group.id = $("#" + controlVarId + "_hGroupId").val();
        group.groupName = $("#" + controlVarId + "_txtName").val();
        group.description = $("#" + controlVarId + "_txtDescription").val();
        group.version = $("#" + controlVarId + "_hVersion").val();
        var roleCodes = [];
        if(roles){
            for(i=0; i<roles.length; i++){
                if($("#" + controlVarId + "_cbxRole_" + roles[i].code).prop("checked")){
                    roleCodes.push({roleCode: roles[i].code});
                }
            }
        }
        group.roleCodes = roleCodes;
        
        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (group.groupName.toLowerCase() === table.data()[i].groupName.toLowerCase()) {
                    alertErrorMessage($.i18n("err-name-exists"));
                    return;
                }
            }
        }

        SystemDao.saveGroup(group, function (response) {
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

    this.deleteGroup = function (rowSelector) {
        if (rowSelector !== null || typeof rowSelector !== 'undefined') {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                SystemDao.deleteGroup(table.row(rowSelector).data().id, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                }, null, true);
            });
        }
    };
};

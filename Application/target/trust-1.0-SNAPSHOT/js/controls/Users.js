/* 
 * System settings control.
 * Requires SystemDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Users = function (controlId, targetElementId) {
    if (controlId === null || typeof controlId === 'undefined') {
        throw "Control id is not provdided";
    }
    if (targetElementId === null || typeof targetElementId === 'undefined') {
        throw "Target element id is not provdided";
    }

    var that = this;
    var table;
    var controlVarId = "__control_users_" + controlId;
    var groups;

    this.init = function () {
        // Load list of roles first
        SystemDao.getGroups(function (groupsList) {
            groups = groupsList;
        }, null, function () {
            // Load control template
            $.get(Global.APP_ROOT + '/js/templates/ControlUsers.html', function (tmpl) {
                var template = Handlebars.compile(tmpl);
                $('#' + targetElementId).html(template({id: controlVarId, groups: groups}));
                // Localize
                $("#" + targetElementId).i18n();

                // Assign control variable
                eval(controlVarId + " = that;");

                SystemDao.getUsers(function (data) {
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
                {data: "userName", title: $.i18n("user-user-name")},
                {data: "fullName", title: $.i18n("user-full-name")},
                {data: "description", title: $.i18n("gen-description")},
                {data: "groupCodes", title: $.i18n("group-groups")},
                {data: "active", title: $.i18n("gen-active")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "250px",
                    "render": function (data, type, full, meta) {
                        return String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteUser($(this).parents('tr'));return false;") +
                                String.format(DataTablesUtility.getEditLink(), controlVarId + ".showUserDialog($(this).parents('tr'));return false;") +
                                " " + data;
                    }
                },
                {
                    targets: 3,
                    "render": function (data, type, full, meta) {
                        return getGroupsList(data);
                    }
                },
                {
                    targets: 4,
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
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showUserDialog(null);return false;"));
    };

    var getGroupsList = function (groupCodes) {
        var groupsList = "";
        if (!isNull(groups) && !isNull(groupCodes)) {
            for (i = 0; i < groupCodes.length; i++) {
                for (j = 0; j < groups.length; j++) {
                    if (groups[j].id === groupCodes[i].groupId) {
                        if (groupsList === "") {
                            groupsList = groups[j].groupName;
                        } else {
                            groupsList = groupsList + ", " + groups[j].groupName;
                        }
                        break;
                    }
                }

            }
        }
        return groupsList;
    };

    var selectedRow = null;

    this.showUserDialog = function (rowSelector) {
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
        $("#" + controlVarId + "_hUserId").val(data.id);
        $("#" + controlVarId + "_txtUserName").val(data.userName);
        $("#" + controlVarId + "_txtFirstName").val(data.firstName);
        $("#" + controlVarId + "_txtLastName").val(data.lastName);
        $("#" + controlVarId + "_txtPassword").val(data.passwd);
        $("#" + controlVarId + "_txtPasswordConfirm").val(data.passwd);
        $("#" + controlVarId + "_cbxActive").prop("checked", (typeof data.active !== 'undefined') ? data.active : true);
        $("#" + controlVarId + "_txtEmail").val(data.email);
        $("#" + controlVarId + "_txtMobileNumber").val(data.mobileNumber);
        $("#" + controlVarId + "_txtDescription").val(data.description);
        $("#" + controlVarId + "_hVersion").val(data.version);
        
        $("#" + controlVarId + "_txtUserName").prop('disabled', (isNullOrEmpty(data.userName)) ? false : true);
        
        if(groups){
            for(i=0; i<groups.length; i++){
                var found = false;
                if(data.groupCodes){
                    for(j=0; j<data.groupCodes.length; j++){
                        if(groups[i].id === data.groupCodes[j].groupId){
                            found = true;
                            break;
                        }
                    }
                }
                $("#" + controlVarId + "_cbxGroup_" + groups[i].id).prop("checked", found);
            }
        }
    };

    this.saveUser = function () {
        // Prepare JSON
        var user = new SystemDao.User();
        user.id = $("#" + controlVarId + "_hUserId").val();
        user.userName = $("#" + controlVarId + "_txtUserName").val();
        user.firstName = $("#" + controlVarId + "_txtFirstName").val();
        user.lastName = $("#" + controlVarId + "_txtLastName").val();
        user.passwd = $("#" + controlVarId + "_txtPassword").val();
        var passConfirm = $("#" + controlVarId + "_txtPasswordConfirm").val();
        user.active = $("#" + controlVarId + "_cbxActive").prop("checked");
        user.email = $("#" + controlVarId + "_txtEmail").val();
        user.mobileNumber = $("#" + controlVarId + "_txtMobileNumber").val();
        user.description = $("#" + controlVarId + "_txtDescription").val();
        user.version = $("#" + controlVarId + "_hVersion").val();
        
        var groupCodes = [];
        if(groups){
            for(i=0; i<groups.length; i++){
                if($("#" + controlVarId + "_cbxGroup_" + groups[i].id).prop("checked")){
                    groupCodes.push({groupId: groups[i].id});
                }
            }
        }
        user.groupCodes = groupCodes;
        
        // Check password
        if(user.passwd !== passConfirm){
            alertErrorMessage($.i18n("err-user-passwords-mismatch"));
            return;
        }
        
        // Check user name
        if (selectedRow === null && table.data()) {
            for (i = 0; i < table.data().length; i++) {
                if (user.userName.toLowerCase() === table.data()[i].userName.toLowerCase()) {
                    alertErrorMessage($.i18n("err-user-exists"));
                    return;
                }
            }
        }
        
        SystemDao.saveUser(user, function (response) {
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

    this.deleteUser = function (rowSelector) {
        if (rowSelector !== null || typeof rowSelector !== 'undefined') {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                SystemDao.deleteUser(table.row(rowSelector).data().id, function () {
                    // Remove from table 
                    table.row(rowSelector).remove().draw();
                    showNotification($.i18n("gen-delete-success"));
                }, null, true);
            });
        }
    };
};

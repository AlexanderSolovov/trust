/* 
 * Applications control.
 * Requires Global.js, SearchDao.js,  ApplicationDao.js
 */
var Controls = Controls || {};

Controls.ApplicationAssign = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var onAssign = isNull(options.onAssign) ? null : options.onAssign;
    var appIds = [];
    var controlVarId = "__control_applicationAssign_" + controlId;
    // Assign control variable
    window[controlVarId] = this;
    
    this.init = function () {
        var html = '<div class="modal fade" id="' + controlVarId + '_Dialog" tabindex="-1" role="dialog" aria-hidden="true"> \
                        <div class="modal-dialog" style="width:400px;"> \
                            <div class="modal-content"> \
                                <div class="modal-header"> \
                                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only" data-i18n="gen-close"></span></button> \
                                    <h4 class="modal-title" data-i18n="app-assignees"></h4> \
                                </div> \
                                <div id="' + controlVarId + '_Body" class="modal-body" style="padding: 0px 5px 0px 5px;"> \
                                    <div class="content"> \
                                        <select id="' + controlVarId + '_cbxAssignees" size="15" class="form-control"></select> \
                                    </div> \
                                </div> \
                                <div class="modal-footer" style="margin-top: 0px;padding: 15px 20px 15px 20px;"> \
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="gen-close"></button> \
                                    <button type="button" id="' + controlVarId + '_btnAssign" class="btn btn-primary" onclick="' + controlVarId + '.assign()" data-i18n="app-assign"></button> \
                                </div> \
                            </div> \
                        </div> \
                    </div>';

        $('#' + targetElementId).html(html);
        // Localize
        $('#' + targetElementId).i18n();
    };

    this.showAssignDialog = function (ids) {
        appIds = ids;
        $("#" + controlVarId + "_Dialog").modal('show');
        if ($('#' + controlVarId + '_cbxAssignees').children('option').length < 1) {
            // Load users list
            SearchDao.searchUsersForAssignment(function (users) {
                if (!isNull(users)) {
                    $.each(users, function (i, user) {
                        $("#" + controlVarId + "_cbxAssignees").append($("<option />").val(user.userName).text(user.fullName));
                    });
                }
            });
        }
    };

    this.assign = function () {
        var assignee = $("#" + controlVarId + "_cbxAssignees").val();
        if (isNullOrEmpty(assignee)) {
            alertErrorMessage($.i18n("err-app-select-assignee"));
            return;
        }
        if (appIds.length < 1) {
            alertErrorMessage($.i18n("err-app-select-apps"));
            return;
        }
        ApplicationDao.assignApplications(appIds, assignee, function () {
            if (isFunction(onAssign)) {
                $("#" + controlVarId + "_Dialog").modal('hide');
                onAssign();
            } else {
                alertSuccessMessage($.i18n("app-assigned"), function () {
                    $("#" + controlVarId + "_Dialog").modal('hide');
                });
            }
        });
    };
};
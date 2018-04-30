/* 
 * Legal entity view control.
 * Requires Documents.js, RefDataDao.js, PartyDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.LegalEntityView = function (controlId, targetElementId, le) {
    validateControl(controlId, targetElementId);

    var that = this;
    var controlVarId = "__control_legal_entity_view_" + controlId;
    var leTypes;
    var loaded = false;
    var docsControl = null;

    this.init = function () {
        // Load ref data
        RefDataDao.getAllRecords(
                RefDataDao.REF_DATA_TYPES.LegalEntityType.type,
                function (list) {
                    leTypes = RefDataDao.filterActiveRecords(list);
                }, null,
                function () {
                    // Load control template
                    $.get(Global.APP_ROOT + '/js/templates/ControlLegalEntityView.html', function (tmpl) {
                        var template = Handlebars.compile(tmpl);
                        $('#' + targetElementId).html(template({id: controlVarId}));
                        // Localize
                        $("#" + targetElementId).i18n();

                        // Assign control variable
                        eval(controlVarId + " = that;");

                        // Add documents component
                        docsControl = new Controls.Documents(controlVarId + "docs", controlVarId + "_divLeDocs", {editable: false});
                        docsControl.init(function () {
                            loaded = true;

                            // Fill in fileds
                            that.setLegalEntity(le);
                        });
                    });
                }, true, true);
    };

    this.setLegalEntity = function (l) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }

        l = isNull(l) ? new PartyDao.Party() : l;

        // Set fields
        var entityType = RefDataDao.getRefDataByCode(leTypes, l.entityTypeCode);
        if (!isNull(entityType)) {
            $("#" + controlVarId + "_lblLeType").text(entityType.val);
        }
        $("#" + controlVarId + "_lblLeName").text(l.name1);
        $("#" + controlVarId + "_lblLeRegNumber").text(l.idNumber);
        $("#" + controlVarId + "_lblLeMobileNumber").text(l.mobileNumber);
        $("#" + controlVarId + "_lblLeAddress").text(l.address);

        if (isNull(l.establishmentDate)) {
            $("#" + controlVarId + "_lblLeRegDate").text("");
        } else {
            $("#" + controlVarId + "_lblLeRegDate").text(dateFormat(l.dob));
        }

        // Set documents
        if (!isNull(docsControl)) {
            docsControl.setDocuments(makeObjectsList(l.documents, "document"));
        }
        
        // Logs
        $("#" + controlVarId + "_listLogs").empty();
        if (l.logs !== null && l.logs.length > 0) {
            $.each(l.logs, function (i, item) {
                var action;
                if(i === 0){
                    action = $.i18n("log-created");
                } else {
                    action = $.i18n("log-edited");
                }
                
                action = String.format(action, "<b>" + item.actionUserName + "</b>", dateFormat(item.actionTime, dateFormat.masks.dateTimeWithSeconds));
                $("#" + controlVarId + "_listLogs").append($("<li />").html(action));
            });
        }
    };

    this.isLoaded = function () {
        return loaded;
    };
    
    this.selectMainTab = function () {
        $("a[href='#" + controlVarId + "_main']").tab('show');
    };
};

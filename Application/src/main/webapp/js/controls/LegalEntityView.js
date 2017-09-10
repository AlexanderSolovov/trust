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
                    $.get(Global.APP_ROOT + '/js/templates/ControlLegalEntity.html', function (tmpl) {
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

        l = isNull(l) ? new PartyDao.LegalEntity() : l;

        // Set fields
        $("#" + controlVarId + "_lblLeType").text(RefDataDao.getRefDataByCode(leTypes, l.entityTypeCode));
        $("#" + controlVarId + "_lblLeName").text(l.name);
        $("#" + controlVarId + "_lblLeRegNumber").text(l.regNumber);
        $("#" + controlVarId + "_lblLeMobileNumber").text(l.mobileNumber);
        $("#" + controlVarId + "_lblLeAddress").text(l.address);

        if (isNull(l.establishmentDate)) {
            $("#" + controlVarId + "_lblLeRegDate").text("");
        } else {
            $("#" + controlVarId + "_lblLeRegDate").text(dateFormat(l.establishmentDate));
        }

        // Set documents
        if (!isNull(docsControl)) {
            docsControl.setDocuments(l.documents);
        }
    };

    this.isLoaded = function () {
        return loaded;
    };
};

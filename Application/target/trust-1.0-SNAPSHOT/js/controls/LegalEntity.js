/* 
 * Legal entity control.
 * Requires Documents.js, RefDataDao.js, PartyDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.LegalEntity = function (controlId, targetElementId, le) {
    validateControl(controlId, targetElementId);

    var that = this;
    var legalEntity = le;
    var controlVarId = "__control_le_" + controlId;
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
                        docsControl = new Controls.Documents(controlVarId + "docs", controlVarId + "_divLeDocs", {editable: true});
                        docsControl.init(function () {
                            loaded = true;

                            // Fill in fileds
                            that.setLegalEntity(legalEntity);
                            bindDateFields();
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
        legalEntity = l;

        // Populate lists
        populateSelectList(leTypes, controlVarId + "_cbxLeTypes");
        
        // Set other fields
        $("#" + controlVarId + "_cbxLeTypes").val(l.entityTypeCode);
        $("#" + controlVarId + "_txtLeName").val(l.name);
        $("#" + controlVarId + "_txtLeRegNumber").val(l.regNumber);
        $("#" + controlVarId + "_txtLeMobileNumber").val(l.mobileNumber);
        $("#" + controlVarId + "_txtLeAddress").val(l.address);

        if (isNull(l.establishmentDate)) {
            $("#" + controlVarId + "_txtLeRegDate").val("");
        } else {
            $("#" + controlVarId + "_txtLeRegDate").val(dateFormat(l.establishmentDate));
        }

        // Set documents
        if (!isNull(docsControl)) {
            docsControl.setDocuments(l.documents);
        }
    };

    this.validate = function (showErrors) {
        var errors = [];

        if (isNullOrEmpty($("#" + controlVarId + "_txtLeName").val())) {
            errors.push($.i18n("err-le-name-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxLeTypes").val())) {
            errors.push($.i18n("err-le-type-empty"));
        }

        if (errors.length > 0) {
            if (showErrors) {
                alertErrorMessages(errors);
            }
            return false;
        }
        return true;
    };

    this.getLegalEntity = function (validate) {
        if (validate) {
            if (!this.validate(true)) {
                return null;
            }
        }

        var result = new PartyDao.LegalEntity();
        if (!isNull(legalEntity)) {
            result.id = legalEntity.id;
            result.version = legalEntity.version;
            result.parentId = legalEntity.parentId;
            result.applicationId = legalEntity.applicationId;
            result.endApplicationId = legalEntity.endApplicationId;
            result.statusCode = legalEntity.statusCode;
            result.editable = legalEntity.editable;
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLeName").val())) {
            result.name = $("#" + controlVarId + "_txtLeName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxLeTypes").val())) {
            result.entityTypeCode = $("#" + controlVarId + "_cbxLeTypes").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLeRegNumber").val())) {
            result.regNumber = $("#" + controlVarId + "_txtLeRegNumber").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLeMobileNumber").val())) {
            result.mobileNumber = $("#" + controlVarId + "_txtLeMobileNumber").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLeAddress").val())) {
            result.address = $("#" + controlVarId + "_txtLeAddress").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLeRegDate").val())) {
            result.establishmentDate = dateFormat($("#" + controlVarId + "_txtLeRegDate").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        return result;
    };
    
    this.isLoaded = function () {
        return loaded;
    };
};

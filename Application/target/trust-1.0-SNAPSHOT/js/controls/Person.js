/* 
 * Person control.
 * Requires Documents.js, RefDataDao.js, PartyDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Person = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var that = this;
    var localPerson = options.person;
    var isOwnership = isNull(options.isOwnership) ? false : options.isOwnership;
    var controlVarId = "__control_person_" + controlId;
    var genders;
    var maritalStatuses;
    var idTypes;
    var citizenships;
    var ownerTypes;
    var loaded = false;
    var docsControl = null;

    this.init = function () {
        // Load ref data
        RefDataDao.getAllRecords(
                RefDataDao.REF_DATA_TYPES.Gender.type,
                function (list) {
                    genders = RefDataDao.filterActiveRecords(list);
                }, null,
                function () {
                    // Marital status
                    RefDataDao.getAllRecords(
                            RefDataDao.REF_DATA_TYPES.MaritalStatus.type,
                            function (list) {
                                maritalStatuses = RefDataDao.filterActiveRecords(list);
                            }, null,
                            function () {
                                // ID Types
                                RefDataDao.getAllRecords(
                                        RefDataDao.REF_DATA_TYPES.IdType.type,
                                        function (list) {
                                            idTypes = RefDataDao.filterActiveRecords(list);
                                        }, null,
                                        function () {
                                            // Citizenship
                                            RefDataDao.getAllRecords(
                                                    RefDataDao.REF_DATA_TYPES.Citizenship.type,
                                                    function (list) {
                                                        citizenships = RefDataDao.filterActiveRecords(list);
                                                    }, null,
                                                    function () {
                                                        if (isOwnership) {
                                                            // Owner type
                                                            RefDataDao.getAllRecords(
                                                                    RefDataDao.REF_DATA_TYPES.OwnerType.type,
                                                                    function (list) {
                                                                        ownerTypes = RefDataDao.filterActiveRecords(list);
                                                                    }, null, loadControl, true, true);
                                                        } else {
                                                            loadControl();
                                                        }
                                                    }, true, true);
                                        }, true, true);
                            }, true, true);
                }, true, true);
    };

    var loadControl = function () {
        // Load control template
        $.get(Global.APP_ROOT + '/js/templates/ControlPerson.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));
            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");

            // Add documents component
            docsControl = new Controls.Documents(controlVarId + "docs", controlVarId + "_divPersonDocs", {editable: true});
            docsControl.init(function () {
                loaded = true;

                // Fill in fileds
                that.setPerson(localPerson);
                bindDateFields();
            });
        });
    };

    this.setPerson = function (p) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }

        p = isNull(p) ? new PartyDao.Party() : p;
        localPerson = p;

        if(isOwnership){
            $("#divPartyRole").show();
        } else {
            $("#divPartyRole").hide();
        }
        
        // Populate lists
        populateSelectList(genders, controlVarId + "_cbxGenders");
        populateSelectList(idTypes, controlVarId + "_cbxIdTypes");
        populateSelectList(citizenships, controlVarId + "_cbxCitezenships");
        populateSelectList(maritalStatuses, controlVarId + "_cbxMaritalStatuses");
        if(isOwnership){
            populateSelectList(ownerTypes, controlVarId + "_cbxOwnerRoles");
            $("#" + controlVarId + "_cbxOwnerRoles").val(p.ownerTypeCode);
            $("#" + controlVarId + "_txtShareSize").val(p.shareSize);
        }

        // Set other fields
        $("#" + controlVarId + "_cbxGenders").val(p.genderCode);
        $("#" + controlVarId + "_cbxIdTypes").val(p.idTypeCode);
        $("#" + controlVarId + "_cbxCitezenships").val(p.citizenshipCode);
        $("#" + controlVarId + "_cbxMaritalStatuses").val(p.maritalStatusCode);

        if (isNull(p.dob)) {
            $("#" + controlVarId + "_txtDob").val("");
        } else {
            $("#" + controlVarId + "_txtDob").val(dateFormat(p.dob));
        }

        $("#" + controlVarId + "_txtFirstName").val(p.name1);
        $("#" + controlVarId + "_txtMiddleName").val(p.name3);
        $("#" + controlVarId + "_txtLastName").val(p.name2);
        $("#" + controlVarId + "_txtIdNumber").val(p.idNumber);
        $("#" + controlVarId + "_txtPersonMobileNumber").val(p.mobileNumber);
        $("#" + controlVarId + "_txtPersonAddress").val(p.address);
        $("#" + controlVarId + "_chbxResident").prop("checked", (typeof p.isResident !== 'undefined') ? p.isResident : true);
        $("#" + controlVarId + "_photoFile").val("");

        showHidePhotoPanel(p.personPhotoId);

        // Set documents
        if (!isNull(docsControl)) {
            docsControl.setDocuments(makeObjectsList(p.documents, "document"));
        }
    };

    var showHidePhotoPanel = function (fileId) {
        if (isNull(fileId)) {
            $("#" + controlVarId + "_photoUploadPanel").show();
            $("#" + controlVarId + "_photoViewPanel").hide();
        } else {
            $("#" + controlVarId + "_photoUploadPanel").hide();
            $("#" + controlVarId + "_photoViewPanel").show();
            $("#" + controlVarId + "_photo").attr("src", String.format(DocumentDao.URL_GET_FILE, fileId));
        }
    };

    this.validate = function (showErrors) {
        var errors = [];

        if (isNullOrEmpty($("#" + controlVarId + "_txtFirstName").val())) {
            errors.push($.i18n("err-person-firstname-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtLastName").val())) {
            errors.push($.i18n("err-person-lastname-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxIdTypes").val())) {
            errors.push($.i18n("err-person-idtype-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtIdNumber").val())) {
            errors.push($.i18n("err-person-idnumber-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_txtDob").val())) {
            errors.push($.i18n("err-person-dob-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxGenders").val())) {
            errors.push($.i18n("err-person-gender-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxCitezenships").val())) {
            errors.push($.i18n("err-person-citizenship-empty"));
        }
        if(isOwnership && isNullOrEmpty($("#" + controlVarId + "_cbxOwnerRoles").val())){
            errors.push($.i18n("err-person-select-role"));
        }
        
        if (errors.length > 0) {
            if (showErrors) {
                alertErrorMessages(errors);
            }
            return false;
        }
        return true;
    };

    this.getPerson = function (onPersonReady, validate) {
        if (validate) {
            if (!this.validate(true)) {
                return;
            }
        }

        var result = new PartyDao.Party();
        if (!isNull(localPerson)) {
            result.id = localPerson.id;
            result.personPhotoId = localPerson.personPhotoId;
            result.version = localPerson.version;
            result.parentId = localPerson.parentId;
            result.applicationId = localPerson.applicationId;
            result.endApplicationId = localPerson.endApplicationId;
            result.statusCode = localPerson.statusCode;
            result.editable = localPerson.editable;
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxGenders").val())) {
            result.genderCode = $("#" + controlVarId + "_cbxGenders").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxIdTypes").val())) {
            result.idTypeCode = $("#" + controlVarId + "_cbxIdTypes").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxCitezenships").val())) {
            result.citizenshipCode = $("#" + controlVarId + "_cbxCitezenships").val();
        }
        if(isOwnership && !isNullOrEmpty($("#" + controlVarId + "_cbxOwnerRoles").val())){
            result.ownerTypeCode = $("#" + controlVarId + "_cbxOwnerRoles").val();
        }
        if(isOwnership && !isNullOrEmpty($("#" + controlVarId + "_txtShareSize").val())){
            result.shareSize = $("#" + controlVarId + "_txtShareSize").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxMaritalStatuses").val())) {
            result.maritalStatusCode = $("#" + controlVarId + "_cbxMaritalStatuses").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDob").val())) {
            result.dob = dateFormat($("#" + controlVarId + "_txtDob").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtFirstName").val())) {
            result.name1 = $("#" + controlVarId + "_txtFirstName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtMiddleName").val())) {
            result.name3 = $("#" + controlVarId + "_txtMiddleName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtLastName").val())) {
            result.name2 = $("#" + controlVarId + "_txtLastName").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtIdNumber").val())) {
            result.idNumber = $("#" + controlVarId + "_txtIdNumber").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtPersonMobileNumber").val())) {
            result.mobileNumber = $("#" + controlVarId + "_txtPersonMobileNumber").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtPersonAddress").val())) {
            result.address = $("#" + controlVarId + "_txtPersonAddress").val();
        }
        result.isResident = $("#" + controlVarId + "_chbxResident").prop("checked");
        updateFullName(result);

        // Set documents
        if (!isNull(docsControl)) {
            var existingDocs = isNull(localPerson) ? null : localPerson.documents;
            result.documents = makeVersionedList(existingDocs, docsControl.getDocuments(), "document");
        }

        // Try to upload photo, if it's provided
        if ($("#" + controlVarId + "_photoUploadPanel").css("display") !== "none" && $("#" + controlVarId + "_photoFile").val() !== "") {
            // Upload
            DocumentDao.uploadFile($("#" + controlVarId + "_photoFile").prop("files")[0], function (response) {
                // Check file id is not empty
                if (isNull(response) || isNullOrEmpty((response.id))) {
                    alertErrorMessage($.i18n("err-doc-upload-failed"));
                    return;
                }
                result.personPhotoId = response.id;
                showHidePhotoPanel(response.id);
                if (isFunction(onPersonReady)) {
                    onPersonReady(result);
                }
            });
        } else {
            if (isFunction(onPersonReady)) {
                onPersonReady(result);
            }
        }
    };

    var updateFullName = function (p) {
        var fullName = String.empty(p.name1);
        if (!isNullOrEmpty(p.name3)) {
            if (!isNullOrEmpty(fullName)) {
                fullName = fullName + " " + p.name3;
            } else {
                fullName = p.name3;
            }
        }
        if (!isNullOrEmpty(p.name2)) {
            if (!isNullOrEmpty(fullName)) {
                fullName = fullName + " " + p.name2;
            } else {
                fullName = p.name2;
            }
        }
        p.fullName = fullName;
    };

    this.deletePhoto = function () {
        if (!isNull(localPerson)) {
            alertConfirm($.i18n("person-confirm-photo-delete"), function () {
                localPerson.personPhotoId = null;
                showHidePhotoPanel(null);
            });
        }
    };

    this.isLoaded = function () {
        return loaded;
    };
};

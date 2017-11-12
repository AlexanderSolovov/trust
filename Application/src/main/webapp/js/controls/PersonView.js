/* 
 * Person view control.
 * Requires Documents.js, RefDataDao.js, PartyDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.PersonView = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var that = this;
    var localPerson = options.person;
    var isOwnership = isNull(options.isOwnership) ? false : options.isOwnership;
    var controlVarId = "__control_person_view_" + controlId;
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
        $.get(Global.APP_ROOT + '/js/templates/ControlPersonView.html', function (tmpl) {
            var template = Handlebars.compile(tmpl);
            $('#' + targetElementId).html(template({id: controlVarId}));
            // Localize
            $("#" + targetElementId).i18n();

            // Assign control variable
            eval(controlVarId + " = that;");

            // Add documents component
            docsControl = new Controls.Documents(controlVarId + "docs", controlVarId + "_divPersonDocs", {editable: false});
            docsControl.init(function () {
                loaded = true;

                // Fill in fileds
                that.setPerson(localPerson);
            });
        });
    };

    this.setPerson = function (p) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }

        p = isNull(p) ? new PartyDao.Person() : p;
        localPerson = p;

        if (isOwnership) {
            $("#divPartyRole").show();
        } else {
            $("#divPartyRole").hide();
        }

        // Set fields
        var gender = RefDataDao.getRefDataByCode(genders, p.genderCode);
        var idType = RefDataDao.getRefDataByCode(idTypes, p.idTypeCode);
        var maritalStatus = RefDataDao.getRefDataByCode(maritalStatuses, p.maritalStatusCode);
        var citizenship = RefDataDao.getRefDataByCode(citizenships, p.citizenshipCode);

        if (isOwnership) {
            var ownerType = RefDataDao.getRefDataByCode(ownerTypes, p.ownerTypeCode);
            if (!isNull(ownerType)) {
                $("#" + controlVarId + "_lblOwnerRole").text(ownerType.val);
            }
            if (!isNull(p.shareSize)) {
                $("#" + controlVarId + "_lblShareSize").text(p.shareSize);
            }
        }

        if (!isNull(gender)) {
            $("#" + controlVarId + "_lblGender").text(gender.val);
        }
        if (!isNull(idType)) {
            $("#" + controlVarId + "_lblIdType").text(idType.val);
        }
        if (!isNull(citizenship)) {
            $("#" + controlVarId + "_lblCitezenship").text(gender.val);
        }
        if (!isNull(maritalStatus)) {
            $("#" + controlVarId + "_lblMaritalStatus").text(maritalStatus.val);
        }

        if (isNull(p.dob)) {
            $("#" + controlVarId + "_lblDob").text("");
        } else {
            $("#" + controlVarId + "_lblDob").text(dateFormat(p.dob));
        }

        $("#" + controlVarId + "_lblFirstName").text(p.name1);
        $("#" + controlVarId + "_lblMiddleName").text(p.name3);
        $("#" + controlVarId + "_lblLastName").text(p.name2);
        $("#" + controlVarId + "_lblIdNumber").text(p.idNumber);
        $("#" + controlVarId + "_lblPersonMobileNumber").text(p.mobileNumber);
        $("#" + controlVarId + "_lblPersonAddress").text(p.address);
        if (typeof p.isResident !== 'undefined' && p.isResident) {
            $("#" + controlVarId + "_lblResident").show();
        } else {
            $("#" + controlVarId + "_lblResident").hide();
        }

        showHidePhotoPanel(p.personPhotoId);

        // Set documents
        if (!isNull(docsControl)) {
            docsControl.setDocuments(makeObjectsList(p.documents, "document"));
        }
    };

    var showHidePhotoPanel = function (fileId) {
        if (isNull(fileId)) {
            $("#" + controlVarId + "_lblNoPhoto").show();
            $("#" + controlVarId + "_photoViewPanel").hide();
        } else {
            $("#" + controlVarId + "_lblNoPhoto").hide();
            $("#" + controlVarId + "_photoViewPanel").show();
            $("#" + controlVarId + "_photo").attr("src", String.format(DocumentDao.URL_GET_FILE, fileId));
        }
    };

    this.isLoaded = function () {
        return loaded;
    };
};

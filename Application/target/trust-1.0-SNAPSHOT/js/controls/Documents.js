/* 
 * Documents control.
 * Requires DocumentDao.js, RefDataDao.js, DatatablesUtility.js
 */
var Controls = Controls || {};

Controls.Documents = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    options = options ? options : {};
    var editable = isNull(options.editable) ? true : options.editable;
    var docs = options.documents ? options.documents : [];
    var that = this;
    var table;
    var controlVarId = "__control_documents_" + controlId;
    var docTypes;
    var docTypesActive;
    var loaded = false;

    this.init = function (onInit) {
        // Load list of document types
        RefDataDao.getAllRecords(RefDataDao.REF_DATA_TYPES.DocumentType.type, function (list) {
            docTypes = list;
            docTypesActive = RefDataDao.filterActiveRecords(docTypes);
        }, null, function () {
            // Load control template
            $.get(Global.APP_ROOT + '/js/templates/ControlDocuments.html', function (tmpl) {
                var template = Handlebars.compile(tmpl);
                $('#' + targetElementId).html(template({id: controlVarId, editable: editable, docTypes: docTypes}));
                // Localize
                $("#" + targetElementId).i18n();

                // Assign control variable
                eval(controlVarId + " = that;");

                loadTable(docs);
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
            "dom": '<"tableToolbar">rt',
            language: DataTablesUtility.getLanguage(),
            columns: [
                {data: "typeCode", title: $.i18n("ref-document-type")},
                {data: "refNumber", title: $.i18n("doc-ref-number")},
                {data: "docDate", title: $.i18n("doc-doc-date")},
                {data: "authority", title: $.i18n("doc-authority")},
                {data: "expiryDate", title: $.i18n("gen-expiry-date")},
                {data: "description", title: $.i18n("gen-description")}
            ],
            columnDefs: [
                {
                    targets: 0,
                    width: "200px",
                    "render": function (data, type, row, meta) {
                        var docType = RefDataDao.getRefDataByCode(docTypes, data);
                        var docTypeName = "";
                        var openFile = "";

                        if (!isNull(docType)) {
                            docTypeName = docType.val;
                        }

                        if (!isNull(row.fileId)) {
                            openFile = '<a href="' + String.format(DocumentDao.URL_GET_FILE, row.fileId)
                                    + '" target="_blank" title="' + $.i18n('gen-open-file') + '" style="padding-right: 5px;"><i class="glyphicon glyphicon-download-alt"></i></a>';
                        }

                        if (editable) {
                            return openFile + "&nbsp;" + String.format(DataTablesUtility.getDeleteLink(), controlVarId + ".deleteDocument($(this).parents('tr'));return false;") +
                                    String.format(DataTablesUtility.getEditLink(), controlVarId + ".showDocumentDialog($(this).parents('tr'));return false;") +
                                    " " + docTypeName;
                        } else {
                            return openFile + " " + docTypeName;
                        }
                    }
                },
                {
                    targets: [2, 4],
                    "render": function (data, type, row, meta) {
                        return dateFormat(data);
                    }
                },
                {
                    targets: 5,
                    width: "200px"
                }
            ]
        });
        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").html(String.format(DataTablesUtility.getAddLink(), controlVarId + ".showDocumentDialog(null);return false;"));
        }
    };

    var selectedRow = null;

    this.showDocumentDialog = function (rowSelector) {
        $("#" + controlVarId + "_Dialog").detach().appendTo('body');
        $("#" + controlVarId + "_Dialog").on("shown.bs.modal", function (event) {
            var tier = $('.modal-dialog').length - 1;
            $('.modal-backdrop').last().css("z-index", 1040 + tier * 30);
            $("#" + controlVarId + "_Dialog").css("z-index", 1050 + tier * 30);
        });

        $("#" + controlVarId + "_Dialog").modal('show');
        
        if (rowSelector === null || typeof rowSelector === 'undefined') {
            selectedRow = null;
            fillForm(null);
        } else {
            selectedRow = table.row(rowSelector);
            fillForm(selectedRow.data());
        }
        bindDateFields();
    };

    var fillForm = function (data) {
        if (data === null) {
            data = {};
        }
        // If doc types empty, fill with values
        populateSelectList(docTypesActive, controlVarId + "_cbxDocTypes");

        $("#" + controlVarId + "_cbxDocTypes").val(data.typeCode);
        $("#" + controlVarId + "_txtRefNumber").val(data.refNumber);

        if (isNull(data.docDate)) {
            $("#" + controlVarId + "_txtDocDate").val("");
        } else {
            $("#" + controlVarId + "_txtDocDate").val(dateFormat(data.docDate));
        }

        if (isNull(data.expiryDate)) {
            $("#" + controlVarId + "_txtExpiryDate").val("");
        } else {
            $("#" + controlVarId + "_txtExpiryDate").val(dateFormat(data.expiryDate));
        }

        $("#" + controlVarId + "_txtAuthority").val(data.authority);
        $("#" + controlVarId + "_txtDescription").val(data.description);
        showHideFilePanel(data.fileId);

        // Enable/diable fields
        $("#" + controlVarId + "_cbxDocTypes").prop('disabled', !editable);
        $("#" + controlVarId + "_txtRefNumber").prop('disabled', !editable);
        $("#" + controlVarId + "_txtDocDate").prop('disabled', !editable);
        $("#" + controlVarId + "_txtExpiryDate").prop('disabled', !editable);
        $("#" + controlVarId + "_txtAuthority").prop('disabled', !editable);
        $("#" + controlVarId + "_txtDescription").prop('disabled', !editable);
        if (!editable) {
            $("#" + controlVarId + "_btnSaveDocument").hide();
        } else {
            $("#" + controlVarId + "_btnSaveDocument").show();
        }
    };

    var showHideFilePanel = function (fileId) {
        if (isNull(fileId)) {
            $("#" + controlVarId + "_fileUploadPanel").show();
            $("#" + controlVarId + "_fileViewPanel").hide();
            $("#" + controlVarId + "_docFile").prop('disabled', !editable);
        } else {
            $("#" + controlVarId + "_fileUploadPanel").hide();
            $("#" + controlVarId + "_fileViewPanel").show();
            $("#" + controlVarId + "_lnkViewFile").attr("href", String.format(DocumentDao.URL_GET_FILE, fileId));
            if (!editable) {
                $("#" + controlVarId + "_lnkDeleteFile").hide();
            } else {
                $("#" + controlVarId + "_lnkDeleteFile").show();
            }
        }
    };

    this.setDocuments = function (list) {
        if (!loaded) {
            alertWarningMessage($.i18n("err-comp-loading"))
            return;
        }
        table.clear();
        docs = list ? list : [];
        table.rows.add(docs);
        table.draw();
    };

    this.getDocuments = function () {
        var docs = [];
        table.rows().data().each(function (d) {
            docs.push(d);
        });
        if (docs.length < 1) {
            return null;
        } else {
            return docs;
        }
    };

    this.saveDocument = function () {
        // Check document type is selected
        if (isNullOrEmpty($("#" + controlVarId + "_cbxDocTypes").val())) {
            alertErrorMessage($.i18n("err-doc-type-empty"));
            return;
        }

        var doc = new DocumentDao.Document();

        // Prepare JSON
        if (selectedRow !== null) {
            doc.id = selectedRow.data().id;
            doc.version = selectedRow.data().version;
            doc.fileId = selectedRow.data().fileId;
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_cbxDocTypes").val())) {
            doc.typeCode = $("#" + controlVarId + "_cbxDocTypes").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtRefNumber").val())) {
            doc.refNumber = $("#" + controlVarId + "_txtRefNumber").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDocDate").val())) {
            doc.docDate = dateFormat($("#" + controlVarId + "_txtDocDate").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtExpiryDate").val())) {
            doc.expiryDate = dateFormat($("#" + controlVarId + "_txtExpiryDate").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtAuthority").val())) {
            doc.authority = $("#" + controlVarId + "_txtAuthority").val();
        }
        if (!isNullOrEmpty($("#" + controlVarId + "_txtDescription").val())) {
            doc.description = $("#" + controlVarId + "_txtDescription").val();
        }

        var addDoc = function () {
            // Close dialog
            $("#" + controlVarId + "_Dialog").modal('hide');
            var currentRow;

            // if selected row is null, then add row
            if (selectedRow === null) {
                currentRow = table.row.add(doc).draw().node();
            } else {
                // Update row
                currentRow = selectedRow.data(doc).draw().node();
            }
            // Animate changed/added row
            highlight(currentRow);
        };

        // Try to upload the file first, if it's provided
        if ($("#" + controlVarId + "_fileUploadPanel").is(':visible')) {
            // Show error if file is not selected
            if (isNullOrEmpty($("#" + controlVarId + "_docFile").val())) {
                alertErrorMessage($.i18n("err-doc-selecte-file"));
                return;
            }
            // Upload
            DocumentDao.uploadFile($("#" + controlVarId + "_docFile").prop("files")[0], function (response) {
                // Check file id is not empty
                if (isNull(response) || isNullOrEmpty((response.id))) {
                    alertErrorMessage($.i18n("err-doc-upload-failed"));
                    return;
                }
                doc.fileId = response.id;
                addDoc();
            });
        } else {
            addDoc();
        }
    };

    this.deleteFile = function () {
        if (selectedRow !== null) {
            alertConfirm($.i18n("doc-confirm-file-delete"), function () {
                selectedRow.data.fileId = null;
                showHideFilePanel(null);
            });
        }
    };

    this.deleteDocument = function (rowSelector) {
        if (rowSelector !== null || typeof rowSelector !== 'undefined') {
            alertConfirm($.i18n("gen-confirm-delete"), function () {
                table.row(rowSelector).remove().draw();
            });
        }
    };
};

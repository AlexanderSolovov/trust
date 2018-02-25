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
                        if (isNull(data)) {
                            return "";
                        } else {
                            if (type === "display") {
                                return dateFormat(data, dateFormat.masks.default);
                            }
                            return data;
                        }
                    }
                },
                {
                    targets: 5,
                    width: "200px"
                }
            ]
        });

        var copyFromApp = "";
        if (!isNull(options.app)) {
            copyFromApp = "&nbsp;&nbsp;" + String.format(DataTablesUtility.getCopyFromAppLink(), controlVarId + ".copyFromApp();return false;");
        }
        $("#" + controlVarId + "_wrapper div.tableToolbar").html(
                String.format(DataTablesUtility.getAddLink(), controlVarId + ".showDocumentDialog(null);return false;") +
                copyFromApp
                );
        if (editable) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }
    };

    var selectedRow = null;

    this.copyFromApp = function () {
        if (!isNull(options.app.documents)) {
            for (var i = 0; i < options.app.documents.length; i++) {
                // Check if it's already exists
                var found = false;
                table.rows().data().each(function (d) {
                    if (String.empty(d.id) === options.app.documents[i].document.id) {
                        found = true;
                    }
                });

                if (!found) {
                    highlight(table.row.add(options.app.documents[i].document).draw().node());
                }
            }
        }
    };

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
        $("#" + controlVarId + "_docFile").val("");
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

    this.setEditable = function (allowEdit) {
        editable = allowEdit;
        if (allowEdit) {
            $("#" + controlVarId + "_wrapper div.tableToolbar").show();
        } else {
            $("#" + controlVarId + "_wrapper div.tableToolbar").hide();
        }
        table.rows().invalidate("data");
        table.draw(false);
    };

    this.getDocuments = function () {
        var documents = [];
        table.rows().data().each(function (d) {
            documents.push(d);
        });
        if (documents.length < 1) {
            return null;
        } else {
            return documents;
        }
    };

    this.saveDocument = function () {
        // Check document type is selected
        if (isNullOrEmpty($("#" + controlVarId + "_cbxDocTypes").val())) {
            alertErrorMessage($.i18n("err-doc-type-empty"));
            return;
        }

        var doc = new DocumentDao.Document();
        var originalDoc = null;
        
        // Prepare JSON
        if (selectedRow !== null) {
            originalDoc = selectedRow.data();
            doc.id = originalDoc.id;
            doc.version = originalDoc.version;
            doc.fileId = originalDoc.fileId;
        }
        
        setStringObjectProperty(originalDoc, doc, "typeCode", controlVarId + "_cbxDocTypes");
        setStringObjectProperty(originalDoc, doc, "refNumber", controlVarId + "_txtRefNumber");
        setDateObjectProperty(originalDoc, doc, "docDate", controlVarId + "_txtDocDate");
        setDateObjectProperty(originalDoc, doc, "expiryDate", controlVarId + "_txtExpiryDate");
        setStringObjectProperty(originalDoc, doc, "authority", controlVarId + "_txtAuthority");
        setStringObjectProperty(originalDoc, doc, "description", controlVarId + "_txtDescription");
               
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
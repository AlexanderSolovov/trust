/* 
 * Contains helper methods for DataTables component
 */
var DataTablesUtility = DataTablesUtility || {};

DataTablesUtility.getAddLink = function () {
    return '<a href="#" onclick="{0}" class="BlueLink">' +
            '<i class="glyphicon glyphicon-plus"></i> ' + $.i18n('gen-add') + '</a>';
};

DataTablesUtility.getEditLink = function () {
    return '<a href="#" title="' + $.i18n('gen-edit') + '" onclick="{0}" class="BlueLink">' +
            '<i class="glyphicon glyphicon-pencil" style="padding-right: 7px;"></i></a>';
};

DataTablesUtility.getDeleteLink = function () {
    return '<a href="#" style="padding-right: 5px;" title="' + $.i18n('gen-delete') + '" onclick="{0}">' +
            '<i class="glyphicon glyphicon-remove"></i></a>';
};

DataTablesUtility.currentLanguage = null;

DataTablesUtility.getLanguage = function () {
    if (DataTablesUtility.currentLanguage === null) {
        DataTablesUtility.currentLanguage = {
            "sEmptyTable": $.i18n("dt-sEmptyTable"),
            "sInfo": $.i18n("dt-sInfo"),
            "sInfoEmpty": $.i18n("dt-sInfoEmpty"),
            "sInfoFiltered": $.i18n("dt-sInfoFiltered"),
            "sInfoPostFix": $.i18n("dt-sInfoPostFix"),
            "sInfoThousands": $.i18n("dt-sInfoThousands"),
            "sLengthMenu": $.i18n("dt-sLengthMenu"),
            "sLoadingRecords": $.i18n("dt-sLoadingRecords"),
            "sProcessing": $.i18n("dt-sProcessing"),
            "sSearch": $.i18n("dt-sSearch"),
            "sZeroRecords": $.i18n("dt-sZeroRecords"),
            "oPaginate": {
                "sFirst": $.i18n("dt-sFirst"),
                "sLast": $.i18n("dt-sLast"),
                "sNext": $.i18n("dt-sNext"),
                "sPrevious": $.i18n("dt-sPrevious")
            },
            "oAria": {
                "sSortAscending": $.i18n("dt-sSortAscending"),
                "sSortDescending": $.i18n("dt-sSortDescending")
            }
        };
    }
    return DataTablesUtility.currentLanguage;
};


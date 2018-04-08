<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="SEARCH_APP_SEARCH" />
    </jsp:attribute>
    <jsp:attribute name="head">
    </jsp:attribute>
    <jsp:body>
        <h1>
            <i class="glyphicon glyphicon-stats"></i>
            <fmt:message key="MENU_REPORTS_APP_STAT" />
        </h1>

        <br />
        <div class="row" style="width: 600px;">
            <div class="col-md-4">
                <label data-i18n="report-date-from"></label>
                <i class="glyphicon glyphicon-required"></i>
                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-calendar"></i>
                    </span>
                    <input id="txtDateFrom" class="form-control DateField" maxlength="10" autocomplete="off">
                </div>
            </div>
            <div class="col-md-4">
                <label data-i18n="report-date-to"></label>
                <i class="glyphicon glyphicon-required"></i>
                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-calendar"></i>
                    </span>
                    <input id="txtDateTo" class="form-control DateField" maxlength="10" autocomplete="off">
                </div>
            </div>
            <div class="col-md-4">
                <label>&nbsp;</label>
                <br />
                <button type="button" class="btn btn-primary" data-i18n="gen-generate" onclick="generateReport()"></button>
            </div>
        </div>

        <script type="text/javascript">
            $(document).ready(function () {
                var today = new Date();
                $("#txtDateTo").val(dateFormat(today));
                $("#txtDateFrom").val(dateFormat(today.setMonth(today.getMonth()-1)));
                bindDateFields();
            });

            function generateReport() {
                var errors = [];

                if (isNullOrEmpty($("#txtDateFrom").val())) {
                    errors.push($.i18n("err-date-from-empty"));
                }
                if (isNullOrEmpty($("#txtDateTo").val())) {
                    errors.push($.i18n("err-date-to-empty"));
                }

                if (errors.length > 0) {
                    alertErrorMessages(errors);
                    return;
                }

                var w = window.open(Global.APP_ROOT + "/ws/" + Global.LANG + "/report/appstat?dateFrom=" + $("#txtDateFrom").val() + "&dateTo=" + $("#txtDateTo").val(), 'ApplicationsStatistics', 'left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,menubar=no,status=no,replace=true');
                if (window.focus) {
                    w.focus();
                }
            }
        </script>
    </jsp:body>
</t:BasePage>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_REPORTS_OCCUPANCY_STAT" />
    </jsp:attribute>
    <jsp:attribute name="head">
    </jsp:attribute>
    <jsp:body>
        <h1>
            <i class="glyphicon glyphicon-stats"></i>
            <fmt:message key="MENU_REPORTS_OCCUPANCY_STAT" />
        </h1>

        <br />
        <button type="button" class="btn btn-primary" data-i18n="gen-generate" onclick="generateReport()"></button>

        <script type="text/javascript">
            function generateReport() {
                var w = window.open(Global.APP_ROOT + "/ws/" + Global.LANG + "/report/ccrobyoccupancy", 'CcroByOccupancy', 'left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,menubar=no,status=no,replace=true');
                if (window.focus) {
                    w.focus();
                }
            }
        </script>
    </jsp:body>
</t:BasePage>
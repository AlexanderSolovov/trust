<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_SETTINGS" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SystemDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Settings.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1><i class="icon-cogs"></i> <fmt:message key="MENU_SETTINGS" /></h1>

        <div id="settingsDiv"></div>

        <script type="text/javascript">
            $(document).ready(function () {
                var ctrlSettings = new Controls.Settings("1", "settingsDiv");
                ctrlSettings.init();
            });
        </script>
    </jsp:body>
</t:BasePage>
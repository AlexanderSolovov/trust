<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${langCode}" />
<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_REF_DISTRICTS" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Districts.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1><i class="glyphicon glyphicon-book"></i> <fmt:message key="MENU_REF_DISTRICTS" /></h1>

        <div id="refDataDiv"></div>

        <script type="text/javascript">
            $(document).ready(function () {
                // Load languages
                loadLanguages(function () {
                    // Create and init control
                    var ctrlDitricts = new Controls.Districts("1", "refDataDiv");
                    ctrlDitricts.init();
                });
            });
        </script>
    </jsp:body>
</t:BasePage>
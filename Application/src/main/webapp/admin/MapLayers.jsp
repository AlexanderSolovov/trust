<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_MAP_LAYERS" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SystemDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/MapLayers.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1><i class="glyphicon glyphicon-map-marker"></i> <fmt:message key="MENU_MAP_LAYERS" /></h1>

        <div id="refDataDiv"></div>

        <script type="text/javascript">
            $(document).ready(function () {
                // Load languages
                loadLanguages(function () {
                    // Create and init control
                    var ctrlMapLayers = new Controls.MapLayers("1", "refDataDiv");
                    ctrlMapLayers.init();
                });
            });
        </script>
    </jsp:body>
</t:BasePage>
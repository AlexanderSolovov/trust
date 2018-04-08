<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_MAP" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/ol/theme/default/style.css" type="text/css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/map.css" type="text/css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/libs/ext/css/ext-all.css" />

        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SystemDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/PropertyDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script src="${pageContext.request.contextPath}/js/libs/ol/OpenLayers.js"></script>
        <script src="${pageContext.request.contextPath}/js/libs/ol/proj4.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/libs/ol/ScaleBar.js"></script>
        <script src="https://maps.google.com/maps/api/js?v=3&amp;key=AIzaSyCReLc9rPj9kPKg1YseVhDKeGt_9z7ZwKk"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/ext/ext-base.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/ext/ext-all.js"></script>
        <script src="${pageContext.request.contextPath}/js/libs/geoext/GeoExt.js"></script>
        <script src="${pageContext.request.contextPath}/js/controls/Map.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/MapCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <div id="mapDiv" style="display: none;">
            <h1 class="h1">
                <i class="glyphicon glyphicon-map-marker"></i> <fmt:message key="MENU_MAP" /> <span id="lblApp" style="font-size: 20px;"></span> 
            </h1>

            <div id="mapToolbar">
                <div class="btn-group" role="group">
                    <button type="button" id="btnBack" class="btn btn-default">
                        <i class="glyphicon glyphicon-arrow-left"></i> <span data-i18n="app-application"></span>
                    </button>
                    <button type="button" id="btnSave" class="btn btn-default">
                        <i class="glyphicon glyphicon-floppy-disk"></i> <span data-i18n="gen-save"></span>
                    </button>
                </div>
                <div class="LineSpace"></div>
            </div>
            <div id="pnlMap"></div>
        </div>
    </jsp:body>
</t:BasePage>
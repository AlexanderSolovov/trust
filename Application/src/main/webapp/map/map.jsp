<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_MAP" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SystemDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/MapCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <div id="mapDiv" style="display: none;">
            <h1>
                <i class="glyphicon glyphicon-map-marker"></i> <fmt:message key="MENU_MAP" /> <span id="lblApp" style="font-size: 20px;"></span> 
            </h1>

            <div id="mapToolbar">
                <div class="btn-group" role="group">
                    <button type="button" id="btnBack" class="btn btn-default">
                        <i class="glyphicon glyphicon-arrow-left"></i> <span data-i18n="gen-back"></span>
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
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="SEARCH_APP_SEARCH" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Applications.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/ApplicationSearch.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1>
            <i class=" glyphicon glyphicon-search"></i>
            <fmt:message key="SEARCH_APP_SEARCH" />
        </h1>
        
        <div id="pnlAppSearch"></div>
        
        <script type="text/javascript">
            $(document).ready(function () {
                var ctrlSearch = new Controls.ApplicationSearch("ctrlAppSearch", "pnlAppSearch");
                ctrlSearch.init();
                
                // Localize
                $("#pnlAppSearch").i18n();
            });
        </script>
    </jsp:body>
</t:BasePage>
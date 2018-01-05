<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="SEARCH_RIGHT_SEARCH" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Rights.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/RightSearch.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1>
            <i class=" glyphicon glyphicon-search"></i>
            <fmt:message key="SEARCH_RIGHT_SEARCH" />
        </h1>
        
        <div id="pnlRightSearch"></div>
        
        <script type="text/javascript">
            $(document).ready(function () {
                var ctrlSearch = new Controls.RightSearch("ctrlRightSearch", "pnlRightSearch");
                ctrlSearch.init();
                
                // Localize
                $("#pnlRightSearch").i18n();
            });
        </script>
    </jsp:body>
</t:BasePage>
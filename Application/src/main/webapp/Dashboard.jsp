<%@page import="com.dai.trust.common.RolesConstants"%>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<%
    if (!request.isUserInRole(RolesConstants.VIEWING)) {
        // Redirect to empty page
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
%>
<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_DASHBOARD" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/ApplicationAssign.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Applications.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/DashboardCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1>
            <i class="glyphicon glyphicon-th-large"></i>
            <fmt:message key="MENU_DASHBOARD" />
        </h1>
        <div id="dashboardDiv" style="display: none;">
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active">
                    <a href="#myApps" aria-controls="myApps" role="tab" data-toggle="tab">
                        <i class="glyphicon glyphicon-briefcase"></i> <span data-i18n="dashboard-my-apps"></span>
                    </a>
                </li>
                <li role="presentation">
                    <a href="#pendingApps" aria-controls="pendingApps" role="tab" data-toggle="tab">
                        <i class="glyphicon glyphicon-time"></i> <span data-i18n="dashboard-pending-apps"></span>
                    </a>
                </li>
            </ul>

            <div class="tab-content">
                <div role="tabpanel" class="tab-pane active" id="myApps">
                    <div class="LineSpace"></div>
                    <div id="pnlMyApps"></div>
                    <div class="LineSpace"></div>
                </div>

                <div role="tabpanel" class="tab-pane" id="pendingApps">
                    <div class="LineSpace"></div>
                    <div id="pnlPendingApps"></div>
                    <div class="LineSpace"></div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:BasePage>
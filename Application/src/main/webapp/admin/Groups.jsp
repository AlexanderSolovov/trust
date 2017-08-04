<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="MENU_GROUPS" />
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SystemDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Groups.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1><i class="icon-cogs"></i> <fmt:message key="MENU_GROUPS" /></h1>

        <div id="groupsDiv"></div>

        <script type="text/javascript">
            $(document).ready(function () {
                var ctrlGroups = new Controls.Groups("1", "groupsDiv");
                ctrlGroups.init();
            });
        </script>
    </jsp:body>
</t:BasePage>
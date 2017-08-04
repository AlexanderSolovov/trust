<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        <fmt:message key="HOMEPAGE_TITLE" />
    </jsp:attribute>
    <jsp:body>
        <h1><fmt:message key="HOMEPAGE_TITLE" /></h1>
    </jsp:body>
</t:BasePage>
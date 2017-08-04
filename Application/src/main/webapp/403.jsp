<%@page import="com.dai.trust.services.refdata.LanguageService"%>
<%@page import="com.dai.trust.exceptions.ExceptionUtility"%>
<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" %>
<%@page import="com.dai.trust.exceptions.TrustException"%>
<%@page import="org.apache.logging.log4j.LogManager"%>
<%@page import="org.apache.logging.log4j.Logger"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    if (session.getAttribute(LanguageService.LANG_SESSION) == null
            || session.getAttribute(LanguageService.LANG_SESSION).equals("")) {

        String langCode = null;

        // Try to load from cookies
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equalsIgnoreCase(LanguageService.LANG_COOKIE)) {
                    langCode = cookie.getValue();
                    break;
                }
            }
        }

        // If no cookie found, get from browser
        if (langCode == null || langCode.equals("")) {
            langCode = request.getLocale().toLanguageTag();
        }

        // If nothing found, set to English
        if (langCode == null || langCode.equals("")) {
            langCode = "en";
        }
    }
%>

<fmt:setLocale value="${langCode}" />
<fmt:setBundle basename="com.dai.trust.strings" />

<%
    Logger logger = LogManager.getLogger("error.jsp");
    logger.error("Error has occured", exception);
%>

<!DOCTYPE html>
<html lang="${langCode}" dir="${not empty ltr ? ltr ? 'ltr' : 'rtl' : 'ltr'}">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>
            <fmt:message key="GENERAL_APP_NAME" /> v<fmt:message key="GENERAL_VERSION" /> - <fmt:message key="ERR_FORBIDDEN" />
        </title>
        <%@include file="WEB-INF/jspf/HeaderResources.jspf" %>
    </head>
    <body>
        <div id="mainPage">
            <%@include file="WEB-INF/jspf/SimpleHeader.jspf" %>
            <%@include file="WEB-INF/jspf/MainMenu.jspf" %>

            <div id="PageContent">
                <div class="contentContainer">
                    <div class="PageName">
                        <i class="glyphicon glyphicon-ban-circle" style="color:red;"></i> <fmt:message key="ERR_FORBIDDEN" />
                    </div>
                    <div style="padding: 0px 15px;">
                        <div class="alert alert-danger" role="alert">
                            <fmt:message key="ERR_INSUFFICIENT_RIGHTS" />
                        </div>
                    </div>
                </div>
            </div>

            <%@include file="WEB-INF/jspf/Footer.jspf" %>
        </div>
    </body>
</html>
<%@tag import="com.dai.trust.services.system.SettingsService"%>
<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@tag import="com.dai.trust.models.refdata.Language"%>
<%@tag import="com.dai.trust.services.refdata.LanguageService"%>
<%@tag import="com.dai.trust.common.StringUtility"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@attribute name="title" fragment="true" required="false" %>
<%@attribute name="head" fragment="true" required="false" %>
<jsp:useBean class="java.util.ArrayList" id="langs" scope="session"></jsp:useBean>
<jsp:useBean class="java.lang.String" id="appRoot" scope="session"></jsp:useBean>
<%
    // Verify version
    SettingsService settingsService = new SettingsService();
    settingsService.verifyVersion(session);
    
    if (session.getAttribute(LanguageService.LANG_SESSION) == null
            || session.getAttribute(LanguageService.LANG_SESSION).equals("")
            || (request.getParameter(LanguageService.LANG_PARAM) != null && !request.getParameter(LanguageService.LANG_PARAM).equals(""))) {

        String langCode = null;
        String langRequestParam = request.getParameter(LanguageService.LANG_PARAM);
        Cookie existinCookie = null;

        if (langRequestParam != null && !langRequestParam.equals("")) {
            langCode = request.getParameter(LanguageService.LANG_PARAM);
        }

        // Try to load from cookies
        if (langCode == null || langCode.equals("")) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookie.getName().equalsIgnoreCase(LanguageService.LANG_COOKIE)) {
                        langCode = cookie.getValue();
                        existinCookie = cookie;
                        break;
                    }
                }
            }
        }

        // If no cookie found, get from browser
        if (langCode == null || langCode.equals("")) {
            langCode = request.getLocale().toLanguageTag();
        }

        // Verify that selected language exists in DB. Otherwise, load default.
        LanguageService langService = new LanguageService();
        
        Language lang = langService.verifyGetLanguage(langCode);
        if (lang != null) {
            langCode = lang.getCode();
            session.setAttribute(LanguageService.LANG_LTR, lang.getLtr());
        } else {
            session.setAttribute(LanguageService.LANG_LTR, true);
        }

        // If nothing found, set to English
        if (langCode == null || langCode.equals("")) {
            langCode = "en";
        }

        // Finally save to session and cookie
        session.setAttribute(LanguageService.LANG_SESSION, langCode);
        session.setAttribute("langs", langService.getLanguages(langCode, true));
        
        Cookie cookie;
        if (existinCookie != null) {
            cookie = existinCookie;
        } else {
            cookie = new Cookie(LanguageService.LANG_COOKIE, langCode);
        }
        cookie.setPath("/");
        cookie.setMaxAge(31536000);
        response.addCookie(cookie);

        // Redirect to the same page in case of postback to avoid re-submit on page refresh
        if (langRequestParam != null && !langRequestParam.equals("")) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                response.sendRedirect(request.getRequestURI() + "?" + queryString);
            } else {
                response.sendRedirect(request.getRequestURI());
            }
        }
    }
%>
<fmt:setLocale value="${langCode}" />
<fmt:setBundle basename="com.dai.trust.strings" />

<!DOCTYPE html>
<html lang="${langCode}" dir="${empty ltr ? 'ltr' : ltr ? 'ltr' : 'rtl'}">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>
            <fmt:message key="GENERAL_APP_NAME" /> v<fmt:message key="GENERAL_VERSION" /> - <jsp:invoke fragment="title"/>
        </title>
        <%@include file="../jspf/HeaderResources.jspf" %>
        <jsp:invoke fragment="head"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    </head>
<body>
    <script type="text/javascript">
        Global.LANG = "${langCode}";
        Global.APP_ROOT = "${pageContext.request.contextPath}";
        Global.USER_NAME = "${pageContext.request.userPrincipal.name}";
        Global.USER_PERMISSIONS.canAdmin = ${pageContext.request.isUserInRole(roles.ADMIN)};
        Global.USER_PERMISSIONS.canApprove = ${pageContext.request.isUserInRole(roles.APPROVE_TRANSACTIONS)};
        Global.USER_PERMISSIONS.canAssign = ${pageContext.request.isUserInRole(roles.ASSIGN_APPLICATIONS)};
        Global.USER_PERMISSIONS.canGenerateTitle = ${pageContext.request.isUserInRole(roles.GENERATE_TITLE)};
        Global.USER_PERMISSIONS.canManageApplications = ${pageContext.request.isUserInRole(roles.MANAGE_APPLICATIONS)};
        Global.USER_PERMISSIONS.canManageOwners = ${pageContext.request.isUserInRole(roles.MANAGE_OWNERS)};
        Global.USER_PERMISSIONS.canManageParcels = ${pageContext.request.isUserInRole(roles.MANAGE_PARCELS)};
        Global.USER_PERMISSIONS.canManageRefData = ${pageContext.request.isUserInRole(roles.MANAGE_REF_DATA)};
        Global.USER_PERMISSIONS.canManageRrr = ${pageContext.request.isUserInRole(roles.MANAGE_RIGHTS)};
        Global.USER_PERMISSIONS.canReAssign = ${pageContext.request.isUserInRole(roles.RE_ASSIGN_APPLICATIONS)};
        Global.USER_PERMISSIONS.canSearch = ${pageContext.request.isUserInRole(roles.SEARCH)};
        Global.USER_PERMISSIONS.canView = ${pageContext.request.isUserInRole(roles.VIEWING)};
        Global.USER_PERMISSIONS.canViewReports = ${pageContext.request.isUserInRole(roles.VIEW_REPORTS)};
        Global.USER_PERMISSIONS.canWithdraw = ${pageContext.request.isUserInRole(roles.WITHDRAW_APPLICATIONS)};
    </script>
    <div id="waitMessage" class="waitMessage"> 
        <img src="${pageContext.request.contextPath}/images/busy.gif" style="vertical-align: middle;">
        <fmt:message key="GENERAL_LABEL_WAIT" />
    </div>
    
    <div class="jGrowl" id="popUpNotification" style="top:110px;left:50%;margin-left:-175px;"></div>
    
    <div id="mainPage">
        <%@include file="../jspf/Header.jspf" %>
        <%@include file="../jspf/MainMenu.jspf" %>
        <div id="pageMessage" class="emptyContainer"></div>

        <div id="PageContent">
            <div class="contentContainer">
                <jsp:doBody/>
                <div id="content" class="emptyContainer"></div>
            </div>
        </div>

        <%@include file="../jspf/Footer.jspf" %>
    </div>
</body>
</html>
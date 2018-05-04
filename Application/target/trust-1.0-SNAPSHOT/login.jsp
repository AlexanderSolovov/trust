<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
        Login Page
    </jsp:attribute>
    <jsp:body>
        <div class="PageName"><i class="glyphicon glyphicon-user"></i> <fmt:message key="LOGINPAGE_TITLE" /></div>
        <c:if test='${not empty param.action}'>
            <script type="text/javascript">
                $(function () {
                    showErrorMessage("<fmt:message key="ERR_LOGIN_FAILED" />");
                });
            </script>
        </c:if>
        <c:if test="${empty pageContext.request.userPrincipal}">
            <div id="LoginBox">
                <form method="POST" action="j_security_check">
                    <div class="form-group">
                        <label for="j_username">
                            <fmt:message key="LOGINPAGE_USERNAME" />
                        </label>
                        <input type="text" name="j_username" id="j_username" class="form-control">
                        <script type="text/javascript">
                            $("#j_username").focus();
                        </script>
                    </div>
                    <div class="form-group">
                        <label for="j_password">
                            <fmt:message key="LOGINPAGE_PASSWORD" />
                        </label>
                        <input type="password" name="j_password" id="j_password" class="form-control">
                    </div>
                    <div style="text-align: right;float: right;">
                        <fmt:message key="LOGINPAGE_SUBMIT" var="submitLabel" />
                        <input type="submit" name="btnSubmit" value="${submitLabel}" class="btn btn-default">
                    </div>
                    <div class="clearfix"></div>
                </form>
            </div>
        </c:if>
        <c:if test="${not empty pageContext.request.userPrincipal}">
            <div id="LoginBox">
                <fmt:message key="LOGINPAGE_USER_ALREADY_LOGGED" />
                <div class="LineSpace"></div>
                <a class="btn btn-default" href="${pageContext.request.contextPath}/logout" role="button">
                    <fmt:message key="LOGINPAGE_LOGOUT" />
                </a>
            </div>
        </c:if>
    </jsp:body>
</t:BasePage>
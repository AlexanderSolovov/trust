<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${langCode}" />
<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/RefData.js"></script>
    </jsp:attribute>
    <jsp:body>
        <h1><i class="glyphicon glyphicon-book"></i> <span id="spanRefDataName"></span></h1>

        <div id="refDataDiv"></div>

        <script type="text/javascript">
            $(document).ready(function () {
                var refDataType = getUrlParam("type");
                if(isNull(refDataType)){
                    showErrorMessage($.i18n("err-ref-type-param-not-found"));
                    return;
                }
                
                // Set title
                var titleText = $.i18n(RefDataDao.REF_DATA_TYPES[refDataType].labelPlural);
                document.title = document.title + " " + titleText;
                $("#spanRefDataName").text(titleText);
                
                // Load languages
                loadLanguages(function () {
                    // Create and init control
                    var refData = new Controls.RefData("1", "refDataDiv", RefDataDao.REF_DATA_TYPES[refDataType]);
                    refData.init();
                });
            });
        </script>
    </jsp:body>
</t:BasePage>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title">
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Documents.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/DocumentDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/PartyDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Persons.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Person.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/PersonView.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/PersonSearch.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntities.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntity.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntityView.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntitySearch.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/ApplicationCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <div id="applicationDiv" style="display: none;">
            <h1>
                <i class="icon-file-alt"></i> <span data-i18n="app-application"></span> <span id="appNumber"></span> 
                <span id="appTypeName"></span>
            </h1>

            <div id="appToolbar">
                <div class="btn-group" role="group">
                    <button type="button" id="btnBack" class="btn btn-default">
                        <i class="glyphicon glyphicon-arrow-left"></i> <span data-i18n="gen-back"></span>
                    </button>
                    <button type="button" class="btn btn-default">
                        <i class="glyphicon glyphicon-floppy-disk"></i> <span data-i18n="gen-save"></span>
                    </button>
                </div>
                <div class="LineSpace"></div>
            </div>

            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active">
                    <a href="#main" aria-controls="main" role="tab" data-toggle="tab">
                        <i class="icon-file"></i> <span data-i18n="gen-main"></span>
                    </a>
                </li>
                <li role="presentation">
                    <a href="#documents" aria-controls="documents" role="tab" data-toggle="tab">
                        <i class="icon-copy"></i> <span data-i18n="doc-documents"></span>
                    </a>
                </li>
                <li role="presentation">
                    <a href="#affected" aria-controls="affected" role="tab" data-toggle="tab">
                        <i class="glyphicon glyphicon-link"></i> <span data-i18n="app-affected-objects"></span>
                    </a>
                </li>
            </ul>

            <div class="tab-content">
                <div role="tabpanel" class="tab-pane active" id="main">
                    <div class="tabContainer">
                        <div class="subSectionHeader" data-i18n="gen-general"></div>

                        <div class="row">
                            <div class="col-md-4">
                                <label data-i18n="app-lodgement-date"></label><br>
                                <span id="appLodgementDate">18/08/2017</span>
                            </div>
                            <div class="col-md-4">
                                <label data-i18n="app-assignee"></label><br>
                                <span id="appAssignee">Alex Smith <br><small>02/15/2017 12:55</small></span>
                            </div>
                            <div class="col-md-4">
                                <label data-i18n="gen-status"></label><br>
                                <span id="appStatus">Pending</span>
                            </div>
                        </div>

                        <div class="LineSpace"></div>

                        <div class="subSectionHeader" data-i18n="app-applicants"></div>

                        <div class="row">
                            <div class="col-md-12">
                                <div id="test"></div>
                                <a href="#" onclick="getPerson();" class="BlueLink"><i class="glyphicon glyphicon-plus"></i> <span data-i18n="gen-add"></span></a>
                            </div>
                        </div>

                        <div class="subSectionHeader" data-i18n="app-ccros"></div>
                        <div class="row">
                            <div class="col-md-12">
                                <a href="#" onclick="alert('delete');return false;" class="deleteIcon"><i class="glyphicon glyphicon-remove"></i></a> 
                                <a href="#">419IRD/13456</a>, 
                                <a href="#" onclick="alert('delete');return false;" class="deleteIcon"><i class="glyphicon glyphicon-remove"></i></a> 
                                <a href="#">419IRD/45648</a> 
                                &nbsp;
                                <a href="#" onclick="alert('!');" class="BlueLink"><i class="glyphicon glyphicon-plus"></i> <span data-i18n="gen-add"></span></a>
                                &nbsp;
                                <a href="#" onclick="alert('!');" class="BlueLink"><i class="glyphicon glyphicon-search"></i> <span data-i18n="gen-search"></span></a>
                            </div>
                        </div>

                        <div class="LineSpace"></div>
                        <div class="LineSpace"></div>

                        <div class="subSectionHeader" data-i18n="gen-comments"></div>
                        <div class="row">
                            <div class="col-md-12">
                                <textarea class="form-control" rows="3"></textarea>
                            </div>
                        </div>

                        <div class="LineSpace"></div>

                        <div class="subSectionHeader" data-i18n="app-rejection-reason"></div>
                        <div id="rejectionReason">

                        </div>

                        <div class="LineSpace"></div>

                        <div class="subSectionHeader" data-i18n="app-withdrawal-reason"></div>
                        <div id="withdrawalReason">

                        </div>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane" id="documents">
                    <div class="tabContainer" style="max-width: 1100px;">
                        <div id="divAppDocs"></div>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane" id="affected">
                    <div class="tabContainer">
                        <div class="subSectionHeader" data-i18n="app-parcels"></div>
                        <ul style="line-height: 30px;">
                            <li>
                                <a href="">419IRD/MUGA/CHMO/12345</a> (created)
                            </li>
                            <li>
                                <a href="">419IRD/MUGA/CHMO/7897</a> (terminated)
                            </li>
                        </ul>

                        <div class="LineSpace"></div>
                        <div class="subSectionHeader" data-i18n="app-ccros"></div>
                        <ul style="line-height: 30px;">
                            <li>
                                <a href="">419IRD/MUGA/12345</a> (created)
                            </li>
                            <li>
                                <a href="">419IRD/MUGA/7897</a> (terminated)
                            </li>
                            <li>
                                <a href="">419IRD/MUGA/45566</a> (modified)
                            </li>
                        </ul>

                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:BasePage>
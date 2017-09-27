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
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/ApplicationAssign.js"></script>
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
                    <button type="button" id="btnSave" class="btn btn-default">
                        <i class="glyphicon glyphicon-floppy-disk"></i> <span data-i18n="gen-save"></span>
                    </button>
                    <button type="button" id="btnEdit" class="btn btn-default">
                        <i class="glyphicon glyphicon-pencil"></i> <span data-i18n="gen-edit"></span>
                    </button>
                    <button type="button" id="btnAssign" class="btn btn-default">
                        <i class="glyphicon glyphicon-share-alt"></i> <span data-i18n="app-assign"></span>
                    </button>
                </div>
                <div class="LineSpace"></div>
            </div>
            
            <div id="pnlAssign"></div>
            
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
                                <span id="lblLodgementDate"></span>
                            </div>
                            <div class="col-md-4">
                                <label data-i18n="app-assignee"></label><br>
                                <span id="lblAssignee"></span>
                                <br>
                                <small><span id="lblAssignmentDate"></span></small>
                            </div>
                            <div class="col-md-4">
                                <label data-i18n="gen-status"></label><br>
                                <span id="appStatus"></span>
                            </div>
                        </div>

                        <div class="LineSpace"></div>

                        <div class="subSectionHeader" data-i18n="app-applicants"></div>

                        <div class="row">
                            <div class="col-md-12">
                                <ul class="nav nav-tabs" role="tablist">
                                    <li role="presentation" class="active">
                                        <a href="#personApplicantTab" aria-controls="main" role="tab" data-toggle="tab">
                                            <i class="glyphicon glyphicon-user"></i> <span data-i18n="person-persons"></span>
                                        </a>
                                    </li>
                                    <li role="presentation">
                                        <a href="#leApplicantTab" aria-controls="documents" role="tab" data-toggle="tab">
                                            <i class="icon-group"></i> <span data-i18n="le-legalentity"></span>
                                        </a>
                                    </li>
                                </ul>
                                
                                 <div class="tab-content">
                                     <div role="tabpanel" class="tab-pane active" id="personApplicantTab">
                                         <div id="pnlPersons"></div>
                                     </div>
                                     <div role="tabpanel" class="tab-pane" id="leApplicantTab">
                                         <div id="pnlLegalEntities"></div>
                                     </div>
                                 </div>
                                <div class="LineSpace"></div>
                            </div>
                        </div>

                        <div id="pnlCcros" style="display: none;">
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

                                    <div class="LineSpace"></div>
                                    <div class="LineSpace"></div>
                                </div>
                            </div>
                        </div>

                        <div class="subSectionHeader" data-i18n="gen-comments"></div>
                        <div class="row">
                            <div class="col-md-12">
                                <textarea class="form-control" rows="3" id="txtComments" style="display: none;"></textarea>
                                <span style="font-style: italic;display: none;" id="lblComments"></span>
                            </div>
                        </div>

                        <div class="LineSpace"></div>

                        <div id="pnlRejectionReason" style="display: none;">
                            <div class="subSectionHeader" data-i18n="app-rejection-reason"></div>
                            <span style="font-style: italic;" id="lblRejectionReason"></span>
                            <div class="LineSpace"></div>
                        </div>

                        <div id="pnlWithdrawalReason" style="display: none;">
                            <div class="subSectionHeader" data-i18n="app-withdrawal-reason"></div>
                            <span style="font-style: italic;" id="lblWithdrawalReason"></span>
                            <div class="LineSpace"></div>
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
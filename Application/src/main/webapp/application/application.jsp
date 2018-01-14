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
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Rights.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/RightSearch.js"></script>
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
                    <button type="button" id="btnApprove" class="btn btn-default">
                        <i class="glyphicon glyphicon-ok"></i> <span data-i18n="gen-approve"></span>
                    </button>
                    <button type="button" id="btnComplete" class="btn btn-default">
                        <i class="glyphicon glyphicon-check"></i> <span data-i18n="gen-complete"></span>
                    </button>
                    <button type="button" id="btnReject" class="btn btn-default">
                        <i class="glyphicon glyphicon-ban-circle"></i> <span data-i18n="gen-reject"></span>
                    </button>
                    <button type="button" id="btnWithdraw" class="btn btn-default">
                        <i class="glyphicon glyphicon-upload"></i> <span data-i18n="gen-withdraw"></span>
                    </button>
                    <button type="button" id="btnAssign" class="btn btn-default">
                        <i class="glyphicon glyphicon-share-alt"></i> <span data-i18n="app-assign"></span>
                    </button>
                    <button type="button" id="btnDrawParcel" class="btn btn-default">
                        <i class="glyphicon glyphicon-map-marker"></i> <span data-i18n="map-edit-map"></span>
                    </button>
                    <button type="button" id="btnManageRights" class="btn btn-default">
                        <i class="glyphicon glyphicon-list-alt"></i> <span data-i18n="prop-prop"></span>
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
                            <div class="col-md-3">
                                <label data-i18n="app-lodgement-date"></label><br>
                                <span id="lblLodgementDate"></span>
                            </div>
                            <div class="col-md-3">
                                <label data-i18n="app-assignee"></label><br>
                                <span id="lblAssignee"></span>
                                <br>
                                <small><span id="lblAssignmentDate"></span></small>
                            </div>
                            <div class="col-md-3">
                                <label data-i18n="gen-status"></label><br>
                                <span id="appStatus"></span>
                                <br>
                                <small><span id="lblStatusDate"></span></small>
                            </div>
                            <div class="col-md-3">
                                <label data-i18n="app-completion-date"></label><br>
                                <span id="lblCompletionDate"></span>
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
                                    <span id="spanCcros"></span>
                                    &nbsp;
                                    <a href="#" id="lnkSearchCcro" onclick="ApplicationCtrl.showPropSearchDialog();return false;" class="BlueLink"><i class="glyphicon glyphicon-search"></i> <span data-i18n="gen-search"></span></a>

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
                        <div id="pnlAffectedParcels" style="display: none;">
                            <div class="subSectionHeader" data-i18n="app-parcels"></div>
                            <ul id="listAffectedParcels" style="line-height: 30px;">
                            </ul>
                            <div class="LineSpace"></div>
                        </div>

                        <div id="pnlAffectedProperties" style="display: none;">
                            <div class="subSectionHeader" data-i18n="app-ccros"></div>
                            <ul id="listAffectedProperties" style="line-height: 30px;">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="propSearchDialog" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" style="width:1000px;">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only" data-i18n="gen-close"></span></button>
                        <h4 class="modal-title" data-i18n="gen-search"></h4>
                    </div>
                    <div id="propSearchBody" class="modal-body" style="padding: 0px 5px 0px 5px;">
                        <div class="content" style="min-height: 580px;">
                            <div id="propSearch"></div>
                        </div>
                    </div>
                    <div class="modal-footer" style="margin-top: 0px;padding: 15px 20px 15px 20px;">
                        <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="gen-close"></button>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:BasePage>
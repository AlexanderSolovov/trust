<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${langCode}" />
<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title"></jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/ol/theme/default/style.css" type="text/css" />

        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/PropertyDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Documents.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/DocumentDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/PartyDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Persons.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Person.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/PersonView.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/PersonSearch.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntities.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntity.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntityView.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/LegalEntitySearch.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controls/Pois.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/PropertyCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <div id="mainDiv" style="display: none;">
            <h1 class="h1">
                <i class="glyphicon glyphicon-list-alt"></i> <span data-i18n="prop-prop"></span> <span id="propNumber"></span>
            </h1>
            <div id="propDiv">
                <div id="propToolbar">
                    <div class="btn-group" role="group">
                        <button type="button" id="btnBack" class="btn btn-default">
                            <i class="glyphicon glyphicon-arrow-left"></i> <span data-i18n="app-application"></span>
                        </button>
                        <button type="button" id="btnDelete" class="btn btn-default">
                            <i class="glyphicon glyphicon-remove"></i> <span data-i18n="gen-delete"></span>
                        </button>
                        <button type="button" id="btnSave" class="btn btn-default">
                            <i class="glyphicon glyphicon-floppy-disk"></i> <span data-i18n="gen-save"></span>
                        </button>
                        <button type="button" id="btnPrintAdjudicationForm" class="btn btn-default">
                            <i class="glyphicon glyphicon-print"></i> <span data-i18n="right-print-adj-form"></span>
                        </button>
                        <button type="button" id="btnPrintCert" class="btn btn-default">
                            <i class="glyphicon glyphicon-print"></i> <span data-i18n="right-print-cert"></span>
                        </button>
                        <button type="button" id="btnPrintTransactionSheet" class="btn btn-default">
                            <i class="glyphicon glyphicon-print"></i> <span data-i18n="right-print-ts"></span>
                        </button>
                    </div>
                    <div class="LineSpace"></div>
                </div>

                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active">
                        <a href="#tabPropMain" aria-controls="tabPropMain" role="tab" data-toggle="tab">
                            <i class="icon-file"></i> <span data-i18n="gen-main"></span>
                        </a>
                    </li>
                    <li role="presentation">
                        <a href="#tabPropRights" aria-controls="tabPropRights" role="tab" data-toggle="tab">
                            <i class="glyphicon glyphicon-briefcase"></i> <span data-i18n="prop-rights"></span>
                        </a>
                    </li>
                    <li role="presentation">
                        <a href="#log" aria-controls="log" role="tab" data-toggle="tab">
                            <i class="glyphicon glyphicon-align-justify"></i> <span data-i18n="log-log"></span>
                        </a>
                    </li>
                </ul>

                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="tabPropMain">
                        <div class="tabContainer">
                            <div class="subSectionHeader" data-i18n="gen-general"></div>

                            <div class="row">
                                <div class="col-md-4">
                                    <label data-i18n="prop-reg-date"></label><br>
                                    <span id="lblPropRegDate"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="prop-file-number"></label><br>
                                    <span id="lblPropFileNumber"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="gen-status"></label><br>
                                    <span id="lblPropStatus"></span>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-4">
                                    <label data-i18n="app-created-by-application"></label><br>
                                    <span id="lblPropCreatedByApp"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="app-terminated-by-application"></label><br>
                                    <span id="lblPropTerminatedByApp"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="prop-termination-date"></label><br>
                                    <span id="lblPropTerminationDate"></span>
                                </div>
                            </div>

                            <div class="LineSpace"></div>
                            <div class="subSectionHeader" data-i18n="parcel-plot"></div>

                            <div class="row">
                                <div class="col-md-4">
                                    <label data-i18n="parcel-uka"></label><br>
                                    <span id="lblParcelUka"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="parcel-survey-date"></label><br>
                                    <span id="lblParcelSurveyDate"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="gen-status"></label><br>
                                    <span id="lblParcelStatus"></span>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-4">
                                    <label data-i18n="parcel-land-type"></label><br>
                                    <span id="lblParcelLandType"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="parcel-location"></label><br>
                                    <span id="lblParcelLocation"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="gen-address"></label><br>
                                    <span id="lblParcelAddress"></span>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-4">
                                    <label data-i18n="app-created-by-application"></label><br>
                                    <span id="lblPaprcelCreatedByApp"></span>
                                </div>
                                <div class="col-md-4">
                                    <label data-i18n="app-terminated-by-application"></label><br>
                                    <span id="lblParcelTerminatedByApp"></span>
                                </div>
                                <div class="col-md-4">
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-12">
                                    <label data-i18n="gen-comments"></label><br>
                                    <span id="lblPaprcelComments"></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div role="tabpanel" class="tab-pane" id="tabPropRights">
                        <div class="tabContainer">
                            <div class="subSectionHeader" data-i18n="prop-active-pending-rights" style="margin-bottom: 0px;"></div>
                            <div id="divAddRight" style="padding-top: 10px;">
                                <a href="#" id="lnkAddRight"><i class="glyphicon glyphicon-plus"></i> <span data-i18n="gen-add"></span></a>
                            </div>
                            <table id="tableActivePendingRights" class="table table-striped table-bordered table-hover white" style="width:100%"></table>

                            <div class="LineSpace"></div>

                            <div class="subSectionHeader" data-i18n="prop-historic-rights" style="margin-bottom: 0px;"></div>
                            <table id="tableHistoricRights" class="table table-striped table-bordered table-hover white" style="width:100%"></table>
                        </div>
                    </div>

                    <div role="tabpanel" class="tab-pane" id="log">
                        <div class="tabContainer">
                            <b><span data-i18n="parcel-plot"></span></b>
                            <ul id="listParcelLogs" style="line-height: 30px;padding-left: 10px;">
                            </ul>
                            
                            <p></p>
                            
                            <b><span data-i18n="prop-prop"></span></b>
                            <ul id="listLogs" style="line-height: 30px;padding-left: 10px;">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div id="rightDiv" style="display: none;">
                <div id="rightToolbar">
                    <div class="btn-group" role="group">
                        <button type="button" id="btnBackToProp" class="btn btn-default" onclick="PropertyCtrl.backToProp()">
                            <i class="glyphicon glyphicon-arrow-left"></i> <span data-i18n="prop-prop"></span>
                        </button>
                        <button type="button" id="btnSaveRight" class="btn btn-default" onclick="PropertyCtrl.saveRight()">
                            <i class="glyphicon glyphicon-floppy-disk"></i> <span data-i18n="gen-save"></span>
                        </button>
                    </div>
                </div>

                <div class="LineSpace"></div>

                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active">
                        <a href="#tabRightMain" aria-controls="tabRightMain" role="tab" data-toggle="tab">
                            <i class="icon-file"></i> <span data-i18n="gen-main"></span>
                        </a>
                    </li>
                    <li role="presentation">
                        <a href="#tabRightholders" aria-controls="tabRightholders" role="tab" data-toggle="tab">
                            <i class="icon-group"></i> <span data-i18n="right-rightholders"></span>
                        </a>
                    </li>
                    <li role="presentation" id="tabPoisHeader">
                        <a href="#tabPois" aria-controls="tabPois" role="tab" data-toggle="tab">
                            <i class="icon-group"></i> <span data-i18n="right-pois"></span>
                        </a>
                    </li>
                    <li role="presentation">
                        <a href="#tabRightDocuments" aria-controls="tabRightDocuments" role="tab" data-toggle="tab">
                            <i class="icon-copy"></i> <span data-i18n="doc-documents"></span>
                        </a>
                    </li>
                    <li role="presentation">
                        <a href="#rightLog" aria-controls="rightLog" role="tab" data-toggle="tab">
                            <i class="glyphicon glyphicon-align-justify"></i> <span data-i18n="log-log"></span>
                        </a>
                    </li>
                </ul>

                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="tabRightMain">
                        <div class="tabContainer" style="max-width: 900px;">
                            <div class="row">
                                <div class="col-md-3">
                                    <label data-i18n="prop-reg-date"></label><br>
                                    <span id="lblRightRegDate"></span>
                                </div>
                                <div class="col-md-3">
                                    <label data-i18n="prop-termination-date"></label><br>
                                    <span id="lblRightTerminationDate"></span>
                                </div>
                                <div class="col-md-3">
                                    <label data-i18n="right-folio-number"></label><br>
                                    <input id="txtFolioNumber" class="form-control" maxlength="20" autocomplete="off">
                                    <span id="lblFolioNumber"></span>
                                </div>
                                <div class="col-md-3">
                                    <label data-i18n="gen-status"></label><br>
                                    <span id="lblRightStatus"></span>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-3">
                                    <label data-i18n="app-created-by-application"></label><br>
                                    <span id="lblRightCreatedByApp"></span>
                                </div>
                                <div class="col-md-3">
                                    <label data-i18n="app-terminated-by-application"></label><br>
                                    <span id="lblRightTerminatedByApp"></span>
                                </div>
                                <div class="col-md-3" id="divWitness1">
                                    <label data-i18n="right-witness1"></label><br>
                                    <input id="txtWitness1" class="form-control" maxlength="250" autocomplete="off">
                                    <span id="lblWitness1"></span>
                                </div>
                                <div class="col-md-3" id="divWitness2">
                                    <label data-i18n="right-witness2"></label><br>
                                    <input id="txtWitness2" class="form-control" maxlength="250" autocomplete="off">
                                    <span id="lblWitness2"></span>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-3" id="divAllocationDate">
                                    <label data-i18n="right-allocation-date"></label>
                                    <i class="glyphicon glyphicon-required"></i>
                                    <br>
                                    <div class="input-group">
                                        <span class="input-group-addon">
                                            <i class="glyphicon glyphicon-calendar"></i>
                                        </span>
                                        <input id="txtAllocationDate" class="form-control DateField" maxlength="10" autocomplete="off">
                                    </div>
                                    <span id="lblAllocationDate"></span>
                                </div>
                                <div class="col-md-3" id="divStartDate">
                                    <label data-i18n="right-start-date"></label>
                                    <i class="glyphicon glyphicon-required"></i>
                                    <br>
                                    <div class="input-group">
                                        <span class="input-group-addon">
                                            <i class="glyphicon glyphicon-calendar"></i>
                                        </span>
                                        <input id="txtStartDate" class="form-control DateField" maxlength="10" autocomplete="off">
                                    </div>
                                    <span id="lblStartDate"></span>
                                </div>
                                <div class="col-md-3" id="divEndDate">
                                    <label data-i18n="right-end-date"></label>
                                    <br>
                                    <div class="input-group">
                                        <span class="input-group-addon">
                                            <i class="glyphicon glyphicon-calendar"></i>
                                        </span>
                                        <input id="txtEndDate" class="form-control DateField" maxlength="10" autocomplete="off">
                                    </div>
                                    <span id="lblEndDate"></span>
                                </div>
                                <div class="col-md-3" id="divDuration">
                                    <label id="lDurationTitle"></label>
                                    <i class="glyphicon glyphicon-required" id="reqDuration"></i>
                                    <br>
                                    <input id="txtDuration" class="form-control" maxlength="5" autocomplete="off" 
                                           onkeypress="return restrictInputDouble(event);" ondrop="return false;" onpaste="return false;">
                                    <span id="lblDuration"></span>
                                </div>
                                <div class="col-md-3" id="divAnnualFee">
                                    <label data-i18n="right-rental-fee"></label><br>
                                    <input id="txtAnnualFee" class="form-control" maxlength="20" autocomplete="off" 
                                           onkeypress="return restrictInputDouble(event);" ondrop="return false;" onpaste="return false;">
                                    <span id="lblAnnualFee"></span>
                                </div>
                                <div class="col-md-3" id="divInteresetRate">
                                    <label data-i18n="right-interest-rate"></label><br>
                                    <input id="txtInteresetRate" class="form-control" maxlength="5" autocomplete="off" 
                                           onkeypress="return restrictInputDouble(event);" ondrop="return false;" onpaste="return false;">
                                    <span id="lblInteresetRate"></span>
                                </div>
                                <div class="col-md-3" id="divDealAmount">
                                    <label data-i18n="right-amount"></label><br>
                                    <input id="txtDealAmount" class="form-control" maxlength="20" autocomplete="off" 
                                           onkeypress="return restrictInputDouble(event);" ondrop="return false;" onpaste="return false;">
                                    <span id="lblDealAmount"></span>
                                </div>
                            </div>

                            <div id="rowAdjudicators">
                                <div class="splitter"></div>

                                <div class="row">
                                    <div class="col-md-3">
                                        <label data-i18n="right-declared-landuse"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <select id="cbxDeclaredLanduse" class="form-control"></select>
                                        <span id="lblDeclaredLanduse"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-approved-landuse"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <select id="cbxApprovedLanduse" class="form-control"></select>
                                        <span id="lblApprovedLanduse"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-adjudicator1"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtAdjudicator1" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblAdjudicator1"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-adjudicator2"></label>
                                        <i class="glyphicon glyphicon-required"></i><br>
                                        <input id="txtAdjudicator2" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblAdjudicator2"></span>
                                    </div>
                                </div>
                            </div>

                            <div id="rowNeigbors">
                                <div class="splitter"></div>

                                <div class="row">
                                    <div class="col-md-3">
                                        <label data-i18n="right-neighbor-north"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtNeighborNorth" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblNeighborNorth"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-neighbor-south"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtNeighborSouth" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblNeighborSouth"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-neighbor-east"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtNeighborEast" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblNeighborEast"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="right-neighbor-west"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtNeighborWest" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblNeighborWest"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="splitter"></div>

                            <div class="row">
                                <div class="col-md-12">
                                    <label data-i18n="gen-description"></label><br>
                                    <textarea id="txtRightDescription" rows="3" class="form-control"></textarea>
                                    <span id="lblRightDescription"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="tabRightholders">
                        <div class="tabContainer" style="max-width: 900px;">
                            <div id="divOccupancyType">
                                <label data-i18n="right-occupancy-type"></label>
                                <i class="glyphicon glyphicon-required"></i><br>
                                <select id="cbxOccupancyType" class="form-control" style="width: 300px;display: inline;" onchange="PropertyCtrl.occupancySelected()"></select>
                                <span id="lblOccupancyType"></span>
                            </div>

                            <div id="divPerson" style="display: none;">
                                <div class="LineSpace"></div>

                                <div class="subSectionHeader" data-i18n="person-persons" style="margin-bottom: 0px;"></div>
                                <div class="LineSpace"></div>
                                <div id="divRightholderPerson"></div>
                            </div>

                            <div id="divLegalEntity" style="display: none;">
                                <div class="LineSpace"></div>

                                <div class="subSectionHeader" data-i18n="le-legalentity" style="margin-bottom: 0px;"></div>
                                <div class="LineSpace"></div>
                                <div id="divRightholderLegalEntity"></div>
                            </div>

                            <div id="divDeceasedPerson" style="display: none;">
                                <div class="LineSpace"></div>

                                <div class="subSectionHeader" data-i18n="right-deceased-person"></div>
                                <div class="row">
                                    <div class="col-md-3">
                                        <label data-i18n="person-first-name"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtDeceasedFirstName" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblDeceasedFirstName"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="person-middle-name"></label>
                                        <br>
                                        <input id="txtDeceasedMiddleName" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblDeceasedMiddleName"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="person-last-name"></label>
                                        <i class="glyphicon glyphicon-required"></i>
                                        <br>
                                        <input id="txtDeceasedLastName" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblDeceasedLastName"></span>
                                    </div>
                                    <div class="col-md-3">
                                        <label data-i18n="gen-description"></label>
                                        <br>
                                        <input id="txtDeceasedDescription" class="form-control" maxlength="250" autocomplete="off">
                                        <span id="lblDeceasedDescription"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="tabPois">
                        <div class="tabContainer">
                            <div id="divPois"></div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="tabRightDocuments">
                        <div class="tabContainer" style="max-width: 1000px;">
                            <div id="divRightDocs"></div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="rightLog">
                        <div class="tabContainer">
                            <ul id="listRightLog" style="line-height: 30px;padding-left: 10px;">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:BasePage>
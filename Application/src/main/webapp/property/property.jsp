<%@page contentType="text/html" pageEncoding="UTF-8" session="true" errorPage="error.jsp" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="com.dai.trust.strings" />

<t:BasePage>
    <jsp:attribute name="title"></jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/ol/theme/default/style.css" type="text/css" />
        
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/SearchDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/PropertyDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/ApplicationDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/dao/RefDataDao.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/controllers/PropertyCtrl.js"></script>
    </jsp:attribute>
    <jsp:body>
        <div id="propDiv" style="display: block;">
            <h1 class="h1">
                <i class="glyphicon glyphicon-list-alt"></i> <span data-i18n="prop-prop"></span> <span id="propNumber"></span>
            </h1>

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
                        <div class="subSectionHeader" data-i18n="prop-active-pending-rights"></div>
                        <a href="#" onclick="AddRight()" id="lnkAddRight"><i class="glyphicon glyphicon-plus"></i> <span data-i18n="gen-add"></span></a>
                        <table id="tableActivePendingRights" class="table table-striped table-bordered table-hover white" style="width:100%">
                            <thead>
                                <th>Type</th>
                                <th>Registration Date</th>
                                <th>Folio Number</th>
                                <th>Rightholder(s)</th>
                                <th>Status</th>
                                <th>&nbsp;</th>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><a href="#">CCRO</a></td>
                                    <td>12/01/2017</td>
                                    <td>#456456</td>
                                    <td>Alexander Smith II</td>
                                    <td>Pending</td>
                                    <td>
                                        <a href="#" onclick="EditRight()"><i class="glyphicon glyphicon-pencil"></i> <span data-i18n="gen-edit"></span></a>
                                        <br>
                                        <a href="#" onclick="deleteRight()"><i class="glyphicon glyphicon-remove"></i> <span data-i18n="gen-delete"></span></a>
                                        <br>
                                        <a href="#" onclick="TransferRight()"><i class="glyphicon glyphicon-transfer"></i> <span data-i18n="right-transfer"></span></a>
                                        <br>
                                        <a href="#" onclick="RemoveRight()"><i class="glyphicon glyphicon-trash"></i> <span data-i18n="right-discharge"></span></a>
                                        <br>
                                        <a href="#" onclick="RemoveRight()"><i class="glyphicon glyphicon-trash"></i> <span data-i18n="gen-remove"></span></a>
                                        <br>
                                        <a href="#" onclick="CancelRemoveRight()"><i class="icon-undo"></i> <span data-i18n="right-cancel-remove"></span></a>
                                        <br>
                                        <a href="#" onclick="CancelRemoveRight()"><i class="icon-undo"></i> <span data-i18n="right-cancel-discharge"></span></a>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        
                        <div class="subSectionHeader" data-i18n="prop-historic-rights"></div>
                        <table id="tableHistoricRights" class="table table-striped table-bordered table-hover white" style="width:100%">
                            <thead>
                                <th>Type</th>
                                <th>Registration Date</th>
                                <th>Termination Date</th>
                                <th>Folio Number</th>
                                <th>Rightholder(s)</th>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><a href="#">CCRO</a></td>
                                    <td>12/01/2017</td>
                                    <td>22/11/2018</td>
                                    <td>#456456</td>
                                    <td>Alexander Smith II</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:BasePage>
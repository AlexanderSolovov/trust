/**
 * Contains methods to manage application page. 
 * Requires RefDataDao.js, ApplicationDao
 */
var ApplicationCtrl = ApplicationCtrl || {};
ApplicationCtrl.Application = new ApplicationDao.Application();
ApplicationCtrl.AppDocs = null;

// Load application information and show it
$(document).ready(function () {
    var app = ApplicationCtrl.Application;
    app.id = getUrlParam("id");
    app.appTypeCode = getUrlParam("type");

    if (!isNull(app.id)) {
        // Load application
    } else {
        // Hide back button for new apps
        $("#btnBack").hide();
    }

    if (isNull(app.appTypeCode)) {
        // Full stop
        showErrorMessage($.i18n("err-app-type-not-found"));
        return;
    }

    // Get app type
    RefDataDao.getRecord(RefDataDao.REF_DATA_TYPES.AppType.type, app.appTypeCode, function (refData) {
        setTile(refData.val, app.appNumber);
    }, function () {
        showErrorMessage($.i18n("err-app-type-not-found"));
        return;
    });

    // Localize
    $("#applicationDiv").i18n();

    // Application documents
    ApplicationCtrl.AppDocs = new Controls.Documents("appDocs", "divAppDocs", {editable: true, documents: [doc]});
    ApplicationCtrl.AppDocs.init();

    var p = new PartyDao.Person();
    p.firstName = "Kolya";
    p.lastName = "Barygin";
    p.fullName = "Kolya Barygin";
    p.idNumber = "12321321"
    p.editable = true;
    p.personPhotoId = 'f9a42536-76ab-4da2-801c-fb7623a5a54f';
    
    var le = new Controls.LegalEntities("les", "test", {editable: true});
    le.init();
//    person = new Controls.Persons("123", "test", {persons: [p], editable: true});
//    person.init();
    //var ps = new Controls.PersonSearch("ps", "test", {selectFunc: function(p){alert(p.name);}, height: 100});
    //ps.init();
        
    $("#applicationDiv").show();
});

function getPerson(){
    alert(person.getPerson(true).name);
}

function setTile(appType, appNumber) {
    if (isNull(appNumber)) {
        appNumber = "#" + $.i18n("gen-new");
    } else {
        appNumber = "#" + appNumber;
    }
    document.title = document.title + " " + $.i18n("app-application") + " " + appNumber + " (" + appType + ")";
    $("#appTypeName").text("(" + appType + ")");
    $("#appNumber").text(appNumber);
}
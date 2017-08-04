// Global variables and functions

var Global = Global || {};
Global.LANG = "en";
Global.LANGUAGES = [];
Global.APP_ROOT = "/";
Global.BLOCK_TIMEOUT;

function createObject(proto) {
    function ctor() {
    }
    ctor.prototype = proto;
    return new ctor();
}

function makeDivSuccess(objId) {
    $("#" + objId).removeClass("has-error has-warning");
    $("#" + objId).addClass("has-success has-feedback");
}

function makeDivError(objId) {
    $("#" + objId).removeClass("has-success has-warning");
    $("#" + objId).addClass("has-error has-feedback");
}

function makeDivWarning(objId) {
    $("#" + objId).removeClass("has-error has-success");
    $("#" + objId).addClass("has-warning has-feedback");
}

function bindDateFields(dtFormat) {
    $(".DateField").datepicker({dateFormat: dtFormat});
    $(".DateField").attr("placeholder", dtFormat.toUpperCase());
    $(".TimeField").attr("placeholder", "HH:MM");
}

function blockUI() {
    $.blockUI({
        message: $('#waitMessage'),
        overlayCSS: {
            backgroundColor: '#fff',
            opacity: 0.6,
            cursor: 'wait'
        },
        css: {
            border: '#41597A solid 1px',
            backgroundColor: '#FFF',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .8,
            color: '#41597A'
        }
    });
}

function unblockUI() {
    $.unblockUI();
}

function showErrorMessages(msgList) {
    var errors = "";
    for (var i = 0; i < msgList.length; i++) {
        errors = errors + "<li>" + msgList[i] + "</li>";
    }

    $("#pageMessage").html('<div class="alert alert-danger">' +
            '<span style="color:#000;">' + $.i18n('err-list-header') + '</span>' +
            '<div class="LineSpace"></div>' +
            '<ul>' + errors + '</ul>' +
            '</div>');
}

function showErrorMessage(msg) {
    $("#pageMessage").html('<div class="alert alert-danger">' + msg + '</div>');
}

function showWarningMessage(msg) {
    $("#pageMessage").html('<div class="alert alert-warning">' + msg + '</div>');
}

function showSuccessMessage(msg) {
    $("#pageMessage").html('<div class="alert alert-success">' + msg + '</div>');
}

function alertErrorMessages(msgList) {
    var errors = "";
    for (var i = 0; i < msgList.length; i++) {
        errors = errors + "<li>" + msgList[i] + "</li>";
    }

    if (errors.length > 0) {
        errors = "<ul>" + errors + "</ul>";
    }
    alertErrorMessage(errors);
}

function alertErrorMessage(msg, action) {
    $.alert({
        title: $.i18n("err-error"),
        content: msg,
        type: 'red',
        typeAnimated: true,
        buttons: {
            close: {
                text: $.i18n("gen-close"),
                action: function () {
                    if (action !== null && typeof action === "function") {
                        action();
                    }
                }
            }
        }
    });
}

function alertSuccessMessage(msg, action) {
    $.alert({
        title: $.i18n("gen-success"),
        content: msg,
        type: 'green',
        typeAnimated: true,
        buttons: {
            close: {
                text: $.i18n("gen-close"),
                action: function () {
                    if (action !== null && typeof action === "function") {
                        action();
                    }
                }
            }
        }
    });
}

function alertWarningMessage(msg, action) {
    $.alert({
        title: $.i18n("gen-warning"),
        content: msg,
        type: 'orange',
        typeAnimated: true,
        buttons: {
            close: {
                text: $.i18n("gen-close"),
                action: function () {
                    if (action !== null && typeof action === "function") {
                        action();
                    }
                }
            }
        }
    });
}

function alertConfirm(msg, action) {
    $.alert({
        title: $.i18n("gen-confirm"),
        content: msg,
        type: 'blue',
        typeAnimated: true,
        buttons: {
            ok: {
                text: $.i18n("gen-ok"),
                btnClass: 'btn-green',
                action: function () {
                    if (action !== null && typeof action === "function") {
                        action();
                    }
                }
            },
            cancel: {
                text: $.i18n("gen-cancel"),
                action: function () {
                }
            }
        }
    });
}

function showTrustErrorMessages(msgList, showAlert) {
    var errors = [];
    for (var i = 0; i < msgList.length; i++) {
        errors.push(msgList[i].message);
    }
    if (errors.length > 1) {
        if (isNull(showAlert) || showAlert) {
            alertErrorMessages(errors);
        } else {
            showErrorMessages(errors);
        }
    } else {
        if (errors.length > 0) {
            if (isNull(showAlert) || showAlert) {
                alertErrorMessage(errors[0]);
            } else {
                showErrorMessage(errors[0]);
            }
        }
    }
}

function showNotification(text) {
    if (typeof text === 'undefined' || text === '' || !$('#popUpNotification').length) {
        return;
    }
    $.jGrowl.defaults.closer = false;
    $('#popUpNotification').jGrowl(text, {theme: 'successNotification', life: 1500, closeTemplate: ''});
}

function handleAjaxError(request, status, showAlert, defaultMessage) {
    if (request.status === 400 && request.hasOwnProperty("responseJSON")) {
        showTrustErrorMessages(request.responseJSON, showAlert);
    } else if (request.status === 401) {
        // Not authenticated, redirect to login
        alertWarningMessage($.i18n("err-not-authenticated"), function () {
            // Try to redirect to the same page, which should trigger login page redirect
            window.location.replace(window.location.href);
        });
    } else if (request.status === 403) {
        // Forbidden 
        alertErrorMessage($.i18n("err-forbidden"));
    } else {
        if (defaultMessage === null || typeof defaultMessage === 'undefined' || defaultMessage === "") {
            defaultMessage = $.i18n("err-unexpected");
        }
        // Show alert by default, even id showAlert is not specified
        if (isNull(showAlert) || showAlert) {
            alertErrorMessage(defaultMessage);
        } else {
            showErrorMessage(defaultMessage);
        }
    }
}

function getAjaxData(url, successAction, failAction, alwaysAction, showErrorAlert) {
    $.ajax({
        url: url,
        type: "GET"
    }).done(function (data) {
        runSafe(function () {
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        });
    }).fail(function (response, status, error) {
        runSafe(function () {
            handleAjaxError(response, status, showErrorAlert, $.i18n("err-failed-get-data"));
            if (failAction !== null && typeof failAction === "function") {
                failAction(response, status);
            }
        });
    }).always(function (response, status, error) {
        runSafe(function () {
            if (alwaysAction !== null && typeof alwaysAction === "function") {
                alwaysAction(response, status, error);
            }
        });
    });
}


function postAjaxData(url, data, successAction, failAction, alwaysAction, showErrorAlert) {
    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        dataType: "json"
    }).done(function (data) {
        runSafe(function () {
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        });
    }).fail(function (request, status, error) {
        runSafe(function () {
            handleAjaxError(request, status, showErrorAlert, $.i18n("err-failed-save-data"));
            if (failAction !== null && typeof failAction === "function") {
                failAction(request, status);
            }
        });
    }).always(function (response, status, error) {
        runSafe(function () {
            if (alwaysAction !== null && typeof alwaysAction === "function") {
                alwaysAction(response, status, error);
            }
        });
    });
}

function deleteAjaxData(url, successAction, failAction, alwaysAction, showErrorAlert) {
    $.ajax({
        url: url,
        type: "DELETE"
    }).done(function (data) {
        runSafe(function () {
            if (successAction !== null && typeof successAction === "function") {
                successAction(data);
            }
        });
    }).fail(function (request, status, error) {
        runSafe(function () {
            handleAjaxError(request, status, showErrorAlert, $.i18n("err-failed-delete-data"));
            if (failAction !== null && typeof failAction === "function") {
                failAction(request, status);
            }
        });
    }).always(function (response, status, error) {
        runSafe(function () {
            if (alwaysAction !== null && typeof alwaysAction === "function") {
                alwaysAction(response, status, error);
            }
        });
    });
}

// Make function to get scripts and keep them in cache
$.cachedScript = function (url, options) {
    options = $.extend(options || {}, {
        dataType: "script",
        cache: true,
        url: url
    });

    return jQuery.ajax(options);
};

// Runs provided function in try-catch block to avoid throwing errors and continue execution.
function runSafe(func) {
    try {
        if (func !== null && typeof func === "function") {
            func();
        }
    } catch (e) {
        console.error(e.message);
    }
}

function highlight(selector) {
    $(selector).clearQueue().queue(function (next) {
        $(this).addClass("highlight");
        next();
    }).delay(500).queue(function (next) {
        $(this).removeClass("highlight");
        next();
    });
}

// Extends String with format function
if (!String.format) {
    String.format = function (format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] !== 'undefined'
                    ? args[number]
                    : match
                    ;
        });
    };
}

/** Extends String with empty method, which returns empty string if object is null or undefined. */
if (!String.empty) {
    String.empty = function (val) {
        if (val === null || typeof val === 'undefined') {
            return "";
        } else {
            return val;
        }
    };
}

/** 
 * Returns true if provided object is null or undefined. 
 * @param obj Object to test.
 */
function isNull(obj) {
    return obj === null || typeof obj === 'undefined';
}

/**
 * Splits provided array into rows with a given number of columns. Produces output like Rows[{columns: [col1, col2]},{columns: [col1, col2]}]
 * @param arr Array of objects to split into rows.
 * @param columnsNumber Number of columns in a row.
 */
function splitArrayInRows(arr, columnsNumber) {
    if (isNull(arr) || isNull(columnsNumber) || columnsNumber < 1) {
        return arr;
    }

    var valRows = [];
    var i = 0;

    for (; ; ) {
        var row = {columns: []};
        for (j = 0; j < columnsNumber; j++) {
            if (i + j < arr.length) {
                row.columns.push(arr[i + j]);
            }
        }

        valRows.push(row);
        i += columnsNumber;

        if (i >= arr.length) {
            break;
        }
    }
    return valRows;
}

/** 
 * Return true if provided string is null, undefined or empty. 
 * @param obj Object to test.
 */
function isNullOrEmpty(obj) {
    return obj === null || typeof obj === 'undefined' || obj === '';
}

function loadLanguages(successAction) {
    if (Global.LANGUAGES === null || Global.LANGUAGES.length < 1) {
        getAjaxData(String.format(RefDataDao.URL_GET_ALL_RECORDS, RefDataDao.REF_DATA_TYPES.Language.type),
                function (data) {
                    if (isNull(data) || data.length < 1) {
                        var lang = new RefDataDao.Language();
                        lang.code = "en";
                        lang.isDefault = true;
                        lang.itemOrder = 1;
                        Global.LANGUAGES = [lang];
                    } else {
                        Global.LANGUAGES = data;
                    }
                    successAction();
                }, null, null, true);
    }
}

/** 
 * Returns localized value from provided unlocalized string. Global.LANG code will be used for localization.
 * Before calling this method, Global.LANGUAGES have to be loaded with loadLanguages method. 
 * @param unlocalizedValue Unlocalized string 
 */
function getLocalizedValue(unlocalizedValue) {
    return getLocalizedValueByLang(unlocalizedValue, Global.LANG);
}

/** 
 * Returns localized value from provided unlocalized string.
 * Before calling this method, Global.LANGUAGES have to be loaded with loadLanguages method. 
 * @param unlocalizedValue Unlocalized string, containg all languages.
 * @param langCode Language code to use for localization.
 */
function getLocalizedValueByLang(unlocalizedValue, langCode) {
    if (isNullOrEmpty(unlocalizedValue) || isNull(Global.LANGUAGES) || Global.LANGUAGES.length < 2 || isNullOrEmpty(langCode)) {
        return unlocalizedValue;
    }

    var localized = unlocalizedValue.split("::::");
    var defaultIndex = -1;

    for (i = 0; i < Global.LANGUAGES.length; i++) {
        if (langCode === Global.LANGUAGES[i].code) {
            if (localized.length > i) {
                return localized[i];
            }
        }
        if (Global.LANGUAGES[i].isDefault) {
            defaultIndex = i;
        }
    }

    // Language not found, try default
    if (defaultIndex > 0 && localized.length > defaultIndex) {
        return localized[i];
    }

    // Nothing found, return initial value
    return unlocalizedValue;
}

/**
 * Returns array of localized strings together with language code (e.g. [[lang: "en", value: "localized value"]].
 * @param unlocalizedValue Unlocalized string, containg all languages.
 */
function getLocalizedArray(unlocalizedValue) {
    var localizedArray = [];
    if (isNull(Global.LANGUAGES) || Global.LANGUAGES.length < 2) {
        if (!isNull(Global.LANGUAGES) && Global.LANGUAGES.length > 0) {
            localizedArray.push({lang: Global.LANGUAGES[0], value: unlocalizedValue});
        } else {
            localizedArray.push({lang: "en", value: unlocalizedValue});
        }
        return localizedArray;
    }

    var localizedStrings = String.empty(unlocalizedValue).split("::::");

    for (i = 0; i < Global.LANGUAGES.length; i++) {
        if (localizedStrings.length > i) {
            localizedArray.push({lang: Global.LANGUAGES[i], value: localizedStrings[i]});
        } else {
            localizedArray.push({lang: Global.LANGUAGES[i], value: ""});
        }
    }
    return localizedArray;
}

/** 
 * Makes unlocalized string, out of provided array with localized strings. 
 * @param localizedArray Array with localized strings. This array is supposed to be sorted by language item order. 
 */
function makeUnlocalizedValue(localizedArray) {
    if (isNull(localizedArray)) {
        return "";
    }
    var unlocalizedString = "";
    for (i = 0; i < localizedArray.length; i++) {
        if (i > 0) {
            unlocalizedString += "::::";
        }
        unlocalizedString += localizedArray[i];
    }
    return unlocalizedString;
}

/**
 * Returns QueryString (URL) paramater by its name
 * @param name Parameter name
 * @param url URL string. If URL is not provided, current window URL will be used
 * @returns Parameter value or null if not found.
 */
function getUrlParam(name, url) {
    if (!url)
        url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
    if (!results)
        return null;
    if (!results[2])
        return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/** 
 * Retricts input for ID or code fields. Restriction pattern is [0-9A-Za-z_\-] 
 * @param e Event object from input field 
 */
function restrictInputForIds(e) {
    var pattern = /[0-9A-Za-z_\-]/g;
    return restrictInput(e, pattern);
}

/** 
 * Retricts input for input fields.
 * @param e Event object from input field 
 * @param pattern Regular expression pattern to use for restriction.
 */
function restrictInput(e, pattern) {
    var keyCode = e.keyCode === 0 ? e.charCode : e.keyCode;
    return (String.fromCharCode(keyCode).match(pattern) ||
            ((e.keyCode === 8 || e.keyCode === 9 || e.keyCode === 46 ||
                    e.keyCode === 36 || e.keyCode === 35 || e.keyCode === 37 ||
                    e.keyCode === 39) && e.charCode !== e.keyCode));
}

// On page load
$(function () {
    // localization
    $.i18n().locale = Global.LANG;
    // Load English by default  
    $.i18n().load(LANGUAGES.en, 'en').done(
            function () {
                // Load second language
                if (Global.LANG !== 'en') {
                    $.i18n().load(LANGUAGES[Global.LANG], Global.LANG).done(
                            function () {
                                $('body').i18n();
                            });
                } else {
                    $('body').i18n();
                }
            });
    $("[rel='tooltip']").tooltip();

    // Handle ajax events
    $(document)
            .ajaxStart(function () {
                Global.BLOCK_TIMEOUT = setTimeout(function () {
                    blockUI();
                }, 1000);
            })
            .ajaxStop(function () {
                clearTimeout(Global.BLOCK_TIMEOUT);
                unblockUI();
            });
});
/* 
 * Contains variours page URLs
 */

var URLS = URLS || {};
$(function () {
    URLS.EDIT_APPLICATION = Global.APP_ROOT + "/application/application.jsp?id={0}";
    URLS.VIEW_APPLICATION = URLS.EDIT_APPLICATION + "&view";
    URLS.VIEW_APPLICATION_WITH_MESSAGE = URLS.VIEW_APPLICATION + "&msg={1}";
    URLS.VIEW_MAP = Global.APP_ROOT + "/map/map.jsp";
    URLS.VIEW_MAP_WITH_PARCEL = URLS.VIEW_MAP + "?parcelId={0}";
    URLS.EDIT_MAP = URLS.VIEW_MAP + "?app={0}";
    URLS.EDIT_MAP_WITH_MESSAGE = URLS.EDIT_MAP + "&msg={1}";
    URLS.EDIT_PROPERTY = Global.APP_ROOT + "/property/property.jsp?appid={0}";
    URLS.EDIT_PROPERTY_WITH_MESSAGE = URLS.EDIT_PROPERTY + "&id={1}&msg={2}";
    URLS.VIEW_PROPERTY = Global.APP_ROOT + "/property/property.jsp?id={0}";
    URLS.VIEW_PROPERTY_BY_RIGHT = Global.APP_ROOT + "/property/property.jsp?rightid={0}";
    URLS.VIEW_PROPERTY_WITH_MESSAGE = URLS.VIEW_PROPERTY + "&msg={1}";
});
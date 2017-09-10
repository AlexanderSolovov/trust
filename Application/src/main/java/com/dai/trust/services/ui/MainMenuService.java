package com.dai.trust.services.ui;

import com.dai.trust.common.SharedData;
import com.dai.trust.models.refdata.AppTypeGroup;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.refdata.LanguageService;
import com.dai.trust.services.refdata.RefDataService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
/**
 * Main menu bean methods
 */
public class MainMenuService extends AbstractService {
    private List<AppTypeGroup> appGroups;
    
    public MainMenuService(){
        super();
    }
    
    public List<AppTypeGroup> getAppGroups(){
        if(appGroups == null){
            RefDataService refService = new RefDataService();
            Object langCodeObj = SharedData.getSession().getAttribute(LanguageService.LANG_SESSION);
            String langCode = null;
            if(langCodeObj != null){
                langCode = langCodeObj.toString();
            }
            appGroups = refService.getRefDataRecords(AppTypeGroup.class, true, langCode);
        }
        return appGroups;
    }
    
    /** 
     * Returns true if application URL contains path. 
     * @param request Request object
     * @param path Path to test
     * @return 
     */
    public boolean containsPath(HttpServletRequest request, String path){
        if((path.equalsIgnoreCase("/index.jsp") || path.equalsIgnoreCase("/")) 
                && request.getRequestURL().toString().equalsIgnoreCase(getApplicationUrl(request) + "/")){
            return true;
        }
        return request.getRequestURL().toString().startsWith(getApplicationUrl(request) + path);
    }
    
    /** 
     * Returns menu item class based on provided path.
     * @param request Request object
     * @param path Path to test
     * @return 
     */
    public String getItemClassByPath(HttpServletRequest request, String path){
        if(containsPath(request, path)){
            return "selectedMenuItem";
        } else {
            return "";
        }
    }
    
    /** 
     * Returns menu hyper link class based on provided path 
     * @param request Request object
     * @param path Path to test
     * @return 
     */
    public String getLinkClassByPath(HttpServletRequest request, String path){
        if(containsPath(request, path)){
            return "padding-bottom:11px !important;";
        } else {
            return "";
        }
    }
}

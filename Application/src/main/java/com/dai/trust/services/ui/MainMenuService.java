package com.dai.trust.services.ui;

import com.dai.trust.services.AbstractService;
import javax.servlet.http.HttpServletRequest;
/**
 * Main menu bean methods
 */
public class MainMenuService extends AbstractService {
    public MainMenuService(){
        super();
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

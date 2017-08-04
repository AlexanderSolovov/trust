package com.dai.trust.services.system;

import com.dai.trust.models.system.AppRole;
import com.dai.trust.services.AbstractService;
import java.util.List;

/**
 * Contains methods, related to managing application roles.
 */
public class AppRoleService extends AbstractService {
    public AppRoleService(){
        super();
    }
    
    /**
     * Returns application role.
     *
     * @return
     */
    public List<AppRole> getRoles() {
        return getAll(AppRole.class, "roleName");
    }
}

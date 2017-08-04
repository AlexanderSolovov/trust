package com.dai.trust.services.system;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.system.AppGroup;
import com.dai.trust.models.system.AppRoleGroup;
import com.dai.trust.services.AbstractService;
import java.util.List;
import java.util.UUID;

/**
 * Contains methods, related to managing application groups.
 */
public class AppGroupService extends AbstractService {

    public AppGroupService() {
        super();
    }

    /**
     * Returns application groups.
     *
     * @return
     */
    public List<AppGroup> getGroups() {
        return getAll(AppGroup.class, "groupName");
    }

    /**
     * Saves group to the database
     *
     * @param group Group object to save
     * @return Returns saved group
     */
    public AppGroup saveGroup(AppGroup group) {
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();

        if (StringUtility.isEmpty(StringUtility.empty(group.getGroupName()).trim())) {
            errors.addError(new TrustException(MessagesKeys.ERR_NAME_EMPTY));
        } else {
            group.setGroupName(group.getGroupName().trim());
        }
        
        if (group.getRoleCodes() == null || group.getRoleCodes().size() < 1){
            errors.addError(new TrustException(MessagesKeys.ERR_GROUP_NO_ROLES));
        }
        
        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        // Assign group id if missing
        if (StringUtility.isEmpty(group.getId())) {
            group.setId(UUID.randomUUID().toString());
        }

        // Assign group id to roles
        if (group.getRoleCodes() != null) {
            for (AppRoleGroup roleGroup : group.getRoleCodes()) {
                if (StringUtility.isEmpty(roleGroup.getGroupId())) {
                    roleGroup.setGroupId(group.getId());
                }
            }
        }
        return save(group, true);
    }
}

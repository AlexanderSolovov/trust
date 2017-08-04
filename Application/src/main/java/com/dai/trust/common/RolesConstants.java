package com.dai.trust.common;

import java.io.Serializable;

/**
 * Holds the list of application roles, used to define access permissions on
 * various methods.
 */
public class RolesConstants implements Serializable {
    // ADMIN
    public static final String ADMIN_ADMIN = "Admin";
    public static final String ADMIN_REF_DATA_MANAGER = "RefDataManager";
    public static final String READER = "Reader";

    public String getADMIN_ADMIN() {
        return ADMIN_ADMIN;
    }

    public String getADMIN_REF_DATA_MANAGER() {
        return ADMIN_REF_DATA_MANAGER;
    }

    public String getREADER() {
        return READER;
    }
}

package com.dai.trust.common;

import java.io.Serializable;

/**
 * Holds the list of application roles, used to define access permissions on
 * various methods.
 */
public class RolesConstants implements Serializable {
    public static final String ADMIN = "Admin";
    public static final String MANAGE_REF_DATA = "ManageRefData";
    public static final String MANAGE_PARCELS = "ManageParcels";
    public static final String MANAGE_APPLICATIONS = "ManageApps";
    public static final String WITHDRAW_APPLICATIONS = "WithdrawApps";
    public static final String ASSIGN_APPLICATIONS = "AssignApps";
    public static final String RE_ASSIGN_APPLICATIONS = "ReAssignApps";
    public static final String VIEWING = "Viewing";
    public static final String MANAGE_RIGHTS = "ManageRights";
    public static final String APPROVE_TRANSACTIONS = "ApproveTrans";
    public static final String GENERATE_TITLE = "GenerateTitle";
    public static final String MANAGE_OWNERS = "ManageOwners";
    public static final String SEARCH = "Search";
    public static final String VIEW_REPORTS = "ViewReports";

    public String getADMIN() {
        return ADMIN;
    }

    public String getMANAGE_REF_DATA() {
        return MANAGE_REF_DATA;
    }

    public String getMANAGE_PARCELS() {
        return MANAGE_PARCELS;
    }

    public String getMANAGE_APPLICATIONS() {
        return MANAGE_APPLICATIONS;
    }

    public String getWITHDRAW_APPLICATIONS() {
        return WITHDRAW_APPLICATIONS;
    }

    public String getASSIGN_APPLICATIONS() {
        return ASSIGN_APPLICATIONS;
    }

    public String getRE_ASSIGN_APPLICATIONS() {
        return RE_ASSIGN_APPLICATIONS;
    }

    public String getVIEWING() {
        return VIEWING;
    }

    public String getMANAGE_RIGHTS() {
        return MANAGE_RIGHTS;
    }

    public String getAPPROVE_TRANSACTIONS() {
        return APPROVE_TRANSACTIONS;
    }

    public String getGENERATE_TITLE() {
        return GENERATE_TITLE;
    }

    public String getMANAGE_OWNERS() {
        return MANAGE_OWNERS;
    }

    public String getSEARCH() {
        return SEARCH;
    }

    public String getVIEW_REPORTS() {
        return VIEW_REPORTS;
    }
}

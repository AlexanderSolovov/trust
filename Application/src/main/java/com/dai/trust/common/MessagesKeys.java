package com.dai.trust.common;

/**
 * Holds list of messages keys, used to extract messages from "strings" bundle
 */
public class MessagesKeys {

// General
    /**
     * Application
     */
    public static final String GENERAL_APP_NAME = "GENERAL_APP_NAME";
    
    /** 0.1 */
    public static final String GENERAL_VERSION = "GENERAL_VERSION";

    // Errors
    /**
     * Unexpected errors have occurred while executing requested action. Please,
     * contact system administrator for more details.
     */
    public static final String ERR_UNEXPECTED_ERROR = "ERR_UNEXPECTED_ERROR";
    /**
     * You don't have rights to access this page or function
     */
    public static final String ERR_INSUFFICIENT_RIGHTS = "ERR_INSUFFICIENT_RIGHTS";
    /**
     * You are not authenticated or your session has expired.
     */
    public static final String ERR_NOT_AUTHENTICATED = "ERR_NOT_AUTHENTICATED";

    /**
     * The following errors have occurred:
     */
    public static final String ERR_LIST_HEADER = "ERR_LIST_HEADER";

    /**
     * Failed to convert json format for {0}
     */
    public static final String ERR_JSON_CONVERSION = "ERR_JSON_CONVERSION";

    /**
     * Name must be provided.
     */
    public static final String ERR_NAME_EMPTY = "ERR_NAME_EMPTY";

    /**
     * Value must be provided.
     */
    public static final String ERR_VALUE_EMPTY = "ERR_VALUE_EMPTY";

    /**
     * Description must be provided.
     */
    public static final String ERR_DESCRIPTION_EMPTY = "ERR_DESCRIPTION_EMPTY";
    
    /** Table name was not found for the class {0}. */
    public static final String ERR_NO_TABLE_NAME = "ERR_NO_TABLE_NAME";
    
    /** Reference data table type {0} was not found. */
    public static final String ERR_REF_DATA_TYPE_NOT_FOUND = "ERR_REF_DATA_TYPE_NOT_FOUND";
    
    /** Code must be provided. */
    public static final String ERR_CODE_EMPTY = "ERR_CODE_EMPTY";
    
    /** Select at least 1 role. */
    public static final String ERR_GROUP_NO_ROLES = "ERR_GROUP_NO_ROLES";
    
    /** Username must be provided. */
    public static final String ERR_USER_USER_NAME_EMPTY = "ERR_USER_USER_NAME_EMPTY"; 
    
    /** Password must be provided. */
    public static final String ERR_USER_PASSWORD_EMPTY = "ERR_USER_PASSWORD_EMPTY"; 
    
    /** First name must be provided. */
    public static final String ERR_USER_FIRST_NAME_EMPTY = "ERR_USER_FIRST_NAME_EMPTY"; 
    
    /** Last name must be provided. */
    public static final String ERR_USER_LAST_NAME_EMPTY = "ERR_USER_LAST_NAME_EMPTY";
    
    /** Select at least 1 group. */
    public static final String ERR_USER_NO_GROUPS = "ERR_USER_NO_GROUPS"; 
    
    /** Your changes cannot be saved as the record you are editing has been changed by someone else. Refresh and try again. */
    public static final String ERR_OPTIMISTIC_LOCK = "ERR_OPTIMISTIC_LOCK";
    
    /** Database version does not match system version. Sync system and database changes. */
    public static final String ERR_VERSIONS_MISMATCH = "ERR_VERSIONS_MISMATCH";
    
    /**
     * File must be attached.
     */
    public static final String ERR_FILE_EMPTY = "ERR_FILE_EMPTY";
    
    /**
     * File size exceeds allowed limit of {0}KB.
     */
    public static final String ERR_FILE_TOO_BIG = "ERR_FILE_TOO_BIG";
    
    /**
     * File type is not allowed. Allowed types are {0}
     */
    public static final String ERR_FILE_RESTRICTED_TYPE = "ERR_FILE_RESTRICTED_TYPE";
    
    /**
     * Failed to read file {0}.
     */
    public static final String ERR_FILE_FAILED_READING = "ERR_FILE_FAILED_READING";
    
    /**
     * Failed to save file {0}.
     */
    public static final String ERR_FILE_FAILED_SAVING = "ERR_FILE_FAILED_SAVING";
    
    /**
     * Path to the media folder was not found.
     */
    public static final String ERR_MEDIA_PATH_NOT_FOUND = "ERR_MEDIA_PATH_NOT_FOUND";
    
    /**
     * Document can not be modified, because it is attached to the approved applications, rights or parties.
     */
    public static final String ERR_DOC_READ_ONLY = "ERR_DOC_READ_ONLY";
    
    /**
     * File must be attached to the document.
     */
    public static final String ERR_DOC_EMPTY_FILE = "ERR_DOC_EMPTY_FILE";
    
    /**
     * Document type must be provided.
     */
    public static final String ERR_DOC_EMPTY_TYPE = "ERR_DOC_EMPTY_TYPE";
}

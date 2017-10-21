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

    /**
     * 0.1
     */
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

    /**
     * Table name was not found for the class {0}.
     */
    public static final String ERR_NO_TABLE_NAME = "ERR_NO_TABLE_NAME";

    /**
     * Reference data table type {0} was not found.
     */
    public static final String ERR_REF_DATA_TYPE_NOT_FOUND = "ERR_REF_DATA_TYPE_NOT_FOUND";

    /**
     * Code must be provided.
     */
    public static final String ERR_CODE_EMPTY = "ERR_CODE_EMPTY";

    /**
     * Select at least 1 role.
     */
    public static final String ERR_GROUP_NO_ROLES = "ERR_GROUP_NO_ROLES";

    /**
     * Username must be provided.
     */
    public static final String ERR_USER_USER_NAME_EMPTY = "ERR_USER_USER_NAME_EMPTY";

    /**
     * Password must be provided.
     */
    public static final String ERR_USER_PASSWORD_EMPTY = "ERR_USER_PASSWORD_EMPTY";

    /**
     * First name must be provided.
     */
    public static final String ERR_USER_FIRST_NAME_EMPTY = "ERR_USER_FIRST_NAME_EMPTY";

    /**
     * Last name must be provided.
     */
    public static final String ERR_USER_LAST_NAME_EMPTY = "ERR_USER_LAST_NAME_EMPTY";

    /**
     * Select at least 1 group.
     */
    public static final String ERR_USER_NO_GROUPS = "ERR_USER_NO_GROUPS";

    /**
     * Your changes cannot be saved as the record you are editing has been
     * changed by someone else. Refresh and try again.
     */
    public static final String ERR_OPTIMISTIC_LOCK = "ERR_OPTIMISTIC_LOCK";

    /**
     * Database version does not match system version. Sync system and database
     * changes.
     */
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
     * Document can not be modified, because it is attached to the approved
     * applications, rights or parties.
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

    /**
     * First name is empty
     */
    public static final String ERR_PERSON_FIRST_NAME_EMPTY = "ERR_PERSON_FIRST_NAME_EMPTY";
    /**
     * Last name is empty
     */
    public static final String ERR_PERSON_LAST_NAME_EMPTY = "ERR_PERSON_LAST_NAME_EMPTY";
    /**
     * ID type is empty
     */
    public static final String ERR_PERSON_ID_TYPE_EMPTY = "ERR_PERSON_ID_TYPE_EMPTY";
    /**
     * ID number is empty
     */
    public static final String ERR_PERSON_ID_NUMBER_EMPTY = "ERR_PERSON_ID_NUMBER_EMPTY";
    /**
     * Date of birth is empty
     */
    public static final String ERR_PERSON_DOB_EMPTY = "ERR_PERSON_DOB_EMPTY";
    /**
     * Gender is empty
     */
    public static final String ERR_PERSON_GENDER_EMPTY = "ERR_PERSON_GENDER_EMPTY";
    /**
     * Citizenship is empty
     */
    public static final String ERR_PERSON_CITIZENSHIP_EMPTY = "ERR_PERSON_CITIZENSHIP_EMPTY";

    /**
     * Person {0} has error in the documents - {1}
     */
    public static final String ERR_PERSON_DOC_ERROR = "ERR_PERSON_DOC_ERROR";

    /**
     * Person {0} cannot be modified, because he/she is involved in the approved
     * applications or registered rights
     */
    public static final String ERR_PERSON_READ_ONLY = "ERR_PERSON_READ_ONLY";

    /**
     * Name is empty
     */
    public static final String ERR_LE_NAME_EMPTY = "ERR_LE_NAME_EMPTY";
    /**
     * Type is empty
     */
    public static final String ERR_LE_TYPE_EMPTY = "ERR_LE_TYPE_EMPTY";

    /**
     * Legal entity {0} has error in the documents - {1}
     */
    public static final String ERR_LE_DOC_ERROR = "ERR_LE_DOC_ERROR";

    /**
     * Legal entity {0} cannot be modified, because it is involved in the
     * approved applications or registered rights
     */
    public static final String ERR_LE_READ_ONLY = "ERR_LE_READ_ONLY";

    /**
     * This application cannot be edited.
     */
    public static final String ERR_APP_READ_ONLY = "ERR_APP_READ_ONLY";

    /**
     * Application type is empty.
     */
    public static final String ERR_APP_TYPE_CODE_EMPTY = "ERR_APP_TYPE_CODE_EMPTY";

    /**
     * CCRO must be provided.
     */
    public static final String ERR_APP_CCRO_EMPTY = "ERR_APP_CCRO_EMPTY";

    /**
     * At least one person must be added as an applicant.
     */
    public static final String ERR_APP_NO_PERSONS = "ERR_APP_NO_PERSONS";
    /**
     * Add one person as a representative of legal entity.
     */
    public static final String ERR_APP_NO_REP = "ERR_APP_NO_REP";
    /**
     * Only one representative (person) is required for legal entity.
     */
    public static final String ERR_APP_ONE_REP_REQUIRED = "ERR_APP_ONE_REP_REQUIRED";
    /**
     * Only one legal entity can be added.
     */
    public static final String ERR_APP_ONE_LE_REQUIRED = "ERR_APP_ONE_LE_REQUIRED";
    /**
     * Application #{0} is archived and cannot be modified or assigned.
     */
    public static final String ERR_APP_ARCHIVED = "ERR_APP_ARCHIVED";
    /**
     * You don't have permissions to assign applications.
     */
    public static final String ERR_NO_ASSIGN_ROLE = "ERR_NO_ASSIGN_ROLE";
    /**
     * You don't have permissions to re-assign applications.
     */
    public static final String ERR_NO_REASSIGN_ROLE = "ERR_NO_REASSIGN_ROLE";
    /**
     * User {0} doesn't exist or active
     */
    public static final String ERR_USER_DONT_EXISTS_OR_ACTIVE = "ERR_USER_DONT_EXISTS_OR_ACTIVE";
    /**
     * Application is not assigned to you.
     */
    public static final String ERR_APP_NOT_ASSIGNED_TO_APPLICATION = "ERR_APP_NOT_ASSIGNED_TO_APPLICATION";
    /**
     * Application ID is not provided.
     */
    public static final String ERR_PARCEL_APPLICATION_EMPTY = "ERR_PARCEL_APPLICATION_EMPTY";
    /**
     * Survey Date is empty.
     */
    public static final String ERR_PARCEL_SURVEY_DATE_EMPTY = "ERR_PARCEL_SURVEY_DATE_EMPTY";
    /**
     * Survey Date cannot be bigger than today.
     */
    public static final String ERR_PARCEL_SURVEY_DATE_IN_FUTURE = "ERR_PARCEL_SURVEY_DATE_IN_FUTURE";
    /**
     * Hamlet is not provided.
     */
    public static final String ERR_PARCEL_HAMLET_EMPTY = "ERR_PARCEL_HAMLET_EMPTY";
    /**
     * Plots have different application ID.
     */
    public static final String ERR_PARCEL_PARCELS_HAVE_DIFFERENT_APPLICATION = "ERR_PARCEL_PARCELS_HAVE_DIFFERENT_APPLICATION";
    /**
     * Plot #{0} cannot be edited
     */
    public static final String ERR_PARCEL_CANNOT_EDIT = "ERR_PARCEL_CANNOT_EDIT";
    /**
     * Plot #{0} cannot be deleted, because it's attached to the land right
     * #{1}.
     */
    public static final String ERR_PARCEL_ATTACHED_CANNOT_DELETE = "ERR_PARCEL_ATTACHED_CANNOT_DELETE";

    /**
     * Application was not found.
     */
    public static final String ERR_APP_NOT_FOUND = "ERR_APP_NOT_FOUND";

    /**
     * Only 1 plot is required for this transaction.
     */
    public static final String ERR_PARCEL_ONE_PARCEL_REQUIRED = "ERR_PARCEL_ONE_PARCEL_REQUIRED";
}

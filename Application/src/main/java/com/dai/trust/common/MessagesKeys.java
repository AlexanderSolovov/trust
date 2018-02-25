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
    
    /**
     * Created
     */
    public static final String GENERAL_CREATED = "GENERAL_CREATED";
    /**
     * Terminated
     */
    public static final String GENERAL_TERMINATED = "GENERAL_TERMINATED";
    /**
     * Modified
     */
    public static final String GENERAL_MODIFIED = "GENERAL_MODIFIED";

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
     * Setting is read-only and cannot be modified.
     */
    public static final String ERR_SETTING_READONLY = "ERR_SETTING_READONLY";

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
     * Middle name is empty
     */
    public static final String ERR_PERSON_MIDDLE_NAME_EMPTY = "ERR_PERSON_MIDDLE_NAME_EMPTY";
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
     * Application must have pending status.
     */
    public static final String ERR_APP_NOT_PENDING = "ERR_APP_NOT_PENDING";
    
    /**
     * Application must have approved status.
     */
    public static final String ERR_APP_NOT_APPROVED = "ERR_APP_NOT_APPROVED";
    
    /**
     * Application must must not have pending status.
     */
    public static final String ERR_APP_PENDING = "ERR_APP_PENDING";

    /**
     * Application type is empty.
     */
    public static final String ERR_APP_TYPE_CODE_EMPTY = "ERR_APP_TYPE_CODE_EMPTY";

    /**
     * CCRO must be provided.
     */
    public static final String ERR_APP_CCRO_EMPTY = "ERR_APP_CCRO_EMPTY";
    
    /**
     * Only 1 CCRO is allowed.
     */
    public static final String ERR_APP_ONE_CCRO_ALLOWED = "ERR_APP_ONE_CCRO_ALLOWED";
    
    /**
     * Only 1 plot is allowed for creation.
     */
    public static final String ERR_APP_ONE_PARCEL_ALLOWED = "ERR_APP_ONE_PARCEL_ALLOWED";

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
     * Property #{0} is being processed by application #{1}. It has to be completed first.
     */
    public static final String ERR_APP_PROP_IN_USE = "ERR_APP_PROP_IN_USE";
    
    /**
     * Property #{0} must have registered status.
     */
    public static final String ERR_APP_PROP_NOT_REGISTERED = "ERR_APP_PROP_NOT_REGISTERED";
    
    /**
     * Property #{0} cannot be removed from the application, since changes were already made.
     */
    public static final String ERR_APP_PROP_CANNOT_REMOVE = "ERR_APP_PROP_IN_USE";
    
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
    
    /** You don't have permissions to approve or reject applications. */
    public static final String ERR_NO_APPROVE_REJECT_ROLE = "ERR_NO_APPROVE_REJECT_ROLE";
    
    /** You don't have permissions to withdraw applications. */
    public static final String ERR_NO_WITHDRAW_ROLE = "ERR_NO_WITHDRAW_ROLE";
    
    /** You don't have permissions to manage applications. */
    public static final String ERR_NO_MANAGE_APP_ROLE = "ERR_NO_MANAGE_APP_ROLE";
        
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
     * There were no changes made under this application. There is nothing to approve.
     */
    public static final String ERR_APP_NO_CHANGES_IN_THE_SYSTEM = "ERR_APP_NO_CHANGES_IN_THE_SYSTEM";

    /** This application must create a plot and CCRO right. */
    public static final String ERR_APP_APPROVE_CCRO_NO_OBJECTS = "ERR_APP_APPROVE_CCRO_NO_OBJECTS";
    
    /**
     * Only 1 plot is required for this transaction.
     */
    public static final String ERR_PARCEL_ONE_PARCEL_REQUIRED = "ERR_PARCEL_ONE_PARCEL_REQUIRED";
    
    /**
     * This property cannot be edited.
     */
    public static final String ERR_PROP_READ_ONLY = "ERR_PROP_READ_ONLY";
    
    /** Property object was not found. */
    public static final String ERR_PROP_NOT_FOUND = "ERR_PROP_NOT_FOUND";
    
    /** Application is not provided or doesn't exist */
    public static final String ERR_PROP_APP_NOT_PROVIDED = "ERR_PROP_APP_NOT_PROVIDED";
    
    /** Property must have pending status */
    public static final String ERR_PROP_MUST_BE_PEDNING = "ERR_PROP_MUST_BE_PEDNING";
    
    /** Property must have registered status. */
    public static final String ERR_PROP_MUST_BE_REGISTERED = "ERR_PROP_MUST_BE_REGISTERED";
    
    /** Property must have registered ownership right. */
    public static final String ERR_PROP_MUST_HAVE_REGISTERED_OWNERSHIP = "ERR_PROP_MUST_HAVE_REGISTERED_OWNERSHIP";
    
    /** Property {0} has pending rights. */
    public static final String ERR_PROP_HAS_PENDING_RIGHTS = "ERR_PROP_HAS_PENDING_RIGHTS";
    
    /** Property {0} has registered mortgages, they have to be discharged first. */
    public static final String ERR_PROP_HAS_REGISTERED_MORTGAGES = "ERR_PROP_HAS_REGISTERED_MORTGAGES";
    
    /** Plot is not provided */
    public static final String ERR_PROP_NO_PARCEL = "ERR_PROP_NO_PARCEL";
    
    /** UKA numbers of new and modified plots are not matching. */
    public static final String ERR_PROP_OLD_NEW_UKA_NOT_MATCHING = "ERR_PROP_OLD_NEW_UKA_NOT_MATCHING";
    
    /** Plot is already attached to the property #{0} */
    public static final String ERR_PROP_PARCEL_ALREADY_INUSE = "ERR_PROP_PARCEL_ALREADY_INUSE";
    
    /** Plot #{0} must have active status. */
    public static final String ERR_PROP_PARCEL_MUST_BE_REGISTERED = "ERR_PROP_PARCEL_MUST_BE_REGISTERED";
    
    /** This property is not listed in the application */
    public static final String ERR_PROP_NOT_IN_APP = "ERR_PROP_NOT_IN_APP";
    
    /** No rights found for saving */
    public static final String ERR_PROP_NO_RIGHTS_FOR_SAVE = "ERR_PROP_NO_RIGHTS_FOR_SAVE";
    
    /** There were found rights, not allowed for saving */
    public static final String ERR_PROP_FOUND_RIGHTS_NOT_FOR_SAVE = "ERR_PROP_FOUND_RIGHTS_NOT_FOR_SAVE";
    
    /** Right type is empty */
    public static final String ERR_PROP_RIGHT_TYPE_EMPTY = "ERR_PROP_RIGHT_TYPE_EMPTY";
    
    /** Right attached to a different property object. */
    public static final String ERR_PROP_RIGHT_HAS_DIFFERENT_PROP = "ERR_PROP_RIGHT_HAS_DIFFERENT_PROP";
    
    /** Right must have registered status. */
    public static final String ERR_PROP_RIGHT_MUST_BE_REGISTERED = "ERR_PROP_RIGHT_MUST_BE_REGISTERED";
    
    /** Right must have pending status. */
    public static final String ERR_PROP_RIGHT_MUST_BE_PENDING = "ERR_PROP_RIGHT_MUST_BE_PENDING";
    
    /** Parent right must be referenced. */
    public static final String ERR_PROP_PARENT_RIGHT_EMPTY = "ERR_PROP_PARENT_RIGHT_EMPTY";
    
    /** Parent right must have registered status. */
    public static final String ERR_PROP_PARENT_RIGHT_MUST_BE_REGISTERED = "ERR_PROP_PARENT_RIGHT_MUST_BE_REGISTERED";
    
    /** Parent right was not found. */
    public static final String ERR_PROP_PARENT_RIGHT_NOT_FOUND = "ERR_PROP_PARENT_RIGHT_NOT_FOUND";
    
    /** Right holders are not provided. */
    public static final String ERR_PROP_NO_RIGHTHOLDERS = "ERR_PROP_NO_RIGHTHOLDERS";
    
    /** Right type was not found. */
    public static final String ERR_PROP_RIGHT_TYPE_NOT_FOUND = "ERR_PROP_RIGHT_TYPE_NOT_FOUND";
    
    /** Multiple "{0}" rights are not allowed. */
    public static final String ERR_PROP_MULTIPLE_RIGHTS_NOT_ALLOWED = "ERR_PROP_MULTIPLE_RIGHTS_NOT_ALLOWED";
    
    /** Allocation Date is empty */
    public static final String ERR_PROP_ALLOCATION_DATE_EMPTY = "ERR_PROP_ALLOCATION_DATE_EMPTY";
    
    /** Allocation Date can't be in future */
    public static final String ERR_PROP_ALLOCATION_DATE_IN_FUTURE = "ERR_PROP_ALLOCATION_DATE_IN_FUTURE";
    
    /** Commencement Date is empty */
    public static final String ERR_PROP_START_DATE_EMPTY = "ERR_PROP_START_DATE_EMPTY";
    
    /** Duration is empty or 0 */
    public static final String ERR_PROP_DURATION_EMPTY = "ERR_PROP_DURATION_EMPTY";
            
    /** Commencement Date can't be greater than Allocation Date */
    public static final String ERR_PROP_START_DATE_GREATER_ALLOCATION = "ERR_PROP_START_DATE_GREATER_ALLOCATION";
    
    /** End Date can't be greater than Commencement Date */
    public static final String ERR_PROP_END_DATE_GREATER_START_DATE = "ERR_PROP_END_DATE_GREATER_START_DATE";
    
    /** Declared Land Use is not selected */
    public static final String ERR_PROP_DECLARED_LANDUSE_EMPTY = "ERR_PROP_DECLARED_LANDUSE_EMPTY";
    
    /** Approved Land Use is not selected */
    public static final String ERR_PROP_APPROVED_LANDUSE_EMPTY = "ERR_PROP_APPROVED_LANDUSE_EMPTY";
    
    /** Adjudicator #1 is empty */
    public static final String ERR_PROP_ADJUDICATOR1_EMPTY = "ERR_PROP_ADJUDICATOR1_EMPTY";
    
    /** Adjudicator #2 is empty */
    public static final String ERR_PROP_ADJUDICATOR2_EMPTY = "ERR_PROP_ADJUDICATOR2_EMPTY";
    
    /** Neighbor (north) is empty */
    public static final String ERR_PROP_NORTH_EMPTY = "ERR_PROP_NORTH_EMPTY";
    
    /** Neighbor (south) is empty */
    public static final String ERR_PROP_SOUTH_EMPTY = "ERR_PROP_SOUTH_EMPTY";
    
    /** Neighbor (east) is empty */
    public static final String ERR_PROP_EAST_EMPTY = "ERR_PROP_EAST_EMPTY";
    
    /** Neighbor (west) is empty */
    public static final String ERR_PROP_WEST_EMPTY = "ERR_PROP_WEST_EMPTY";
    
    /** Occupancy Type is not selected */
    public static final String ERR_PROP_OCCUPANCY_TYPE_EMPTY = "ERR_PROP_OCCUPANCY_TYPE_EMPTY";
    
    /** Deceased person is not provided */
    public static final String ERR_PROP_DP_EMPTY = "ERR_PROP_DP_EMPTY";
    
    /** Deceased person first name is empty */
    public static final String ERR_PROP_DP_FIRST_NAME_EMPTY = "ERR_PROP_DP_FIRST_NAME_EMPTY";
    
    /** Deceased person last name is empty */
    public static final String ERR_PROP_DP_LAST_NAME_EMPTY = "ERR_PROP_DP_LAST_NAME_EMPTY";
    
    /** Legal Entity is not provided */
    public static final String ERR_PROP_LE_EMPTY = "ERR_PROP_LE_EMPTY";
    
    /** Only one Legal Entity is allowed */
    public static final String ERR_PROP_ONE_LE_ALLOWED = "ERR_PROP_ONE_LE_ALLOWED";
    
    /** Share size must be provided for {0} */
    public static final String ERR_PROP_SHARE_SIZE_EMPTY = "ERR_PROP_SHARE_SIZE_EMPTY";
    
    /** {0} must be older than 18 */
    public static final String ERR_PROP_YOUNG_OWNER = "ERR_PROP_YOUNG_OWNER";
    
    /** {0} must be younger than 18 */
    public static final String ERR_PROP_OLD_OWNER = "ERR_PROP_OLD_OWNER";
    
    /** One owner must be provided. Make sure owner role is assigned to the person. No guardians or administrators allowed. */
    public static final String ERR_PROP_ONE_OWNER_ALLOWED = "ERR_PROP_ONE_OWNER_ALLOWED";
    
    /** At least one administrator must be provided. Make sure administrator role is assigned to the person. No guardians or owners allowed. */
    public static final String ERR_PROP_ONE_OR_MANY_ADMINS_ALLOWED = "ERR_PROP_ONE_OR_MANY_ADMINS_ALLOWED";
    
    /** At least two owners must be provided. Make sure owner role is assigned to the persons. No guardians or administrators allowed. */
    public static final String ERR_PROP_MULTIPLE_OWNERS_REQUIRED = "ERR_PROP_MULTIPLE_OWNERS_REQUIRED";
    
    /** At least one minor and one guardian must be provided. Make sure owner and guardian roles are assigned to the persons. No administrators allowed. */
    public static final String ERR_PROP_WRONG_GUARDINASHIP = "ERR_PROP_WRONG_GUARDINASHIP";
    
    /** Party is not provided */
    public static final String ERR_PROP_NO_PARTY = "ERR_PROP_NO_PARTY";
    
    /** Person of interest first name is empty. */
    public static final String ERR_PROP_POI_FIRST_NAME_EMPTY = "ERR_PROP_POI_FIRST_NAME_EMPTY";
    
    /** Person of interest last name is empty. */
    public static final String ERR_PROP_POI_LAST_NAME_EMPTY = "ERR_PROP_POI_LAST_NAME_EMPTY";
    
    /** Rightholders must be same as on the parent right. */
    public static final String ERR_PROP_RIGHTHOLDERS_MUST_BE_SAME = "ERR_PROP_RIGHTHOLDERS_MUST_BE_SAME";
}

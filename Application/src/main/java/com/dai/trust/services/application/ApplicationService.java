package com.dai.trust.services.application;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.RolesConstants;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StatusCodeConstants;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.application.Application;
import com.dai.trust.models.application.ApplicationBasic;
import com.dai.trust.models.application.ApplicationDocument;
import com.dai.trust.models.application.ApplicationNumber;
import com.dai.trust.models.application.ApplicationParty;
import com.dai.trust.models.application.ApplicationPermissions;
import com.dai.trust.models.application.ApplicationProperty;
import com.dai.trust.models.application.ApplicationStatusChanger;
import com.dai.trust.models.party.Party;
import com.dai.trust.models.party.PartyDocument;
import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.property.ParcelStatusChanger;
import com.dai.trust.models.property.PropertyStatusChanger;
import com.dai.trust.models.property.RrrStatusChanger;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.TransactionType;
import com.dai.trust.models.search.AffectedObjectSearchResult;
import com.dai.trust.models.search.ApplicationNumberSearchResult;
import com.dai.trust.models.search.PropertyCodeSearchResult;
import com.dai.trust.models.system.User;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.document.DocumentService;
import com.dai.trust.services.party.PartyService;
import com.dai.trust.services.property.PropertyService;
import com.dai.trust.services.refdata.RefDataService;
import com.dai.trust.services.search.SearchService;
import com.dai.trust.services.system.UserService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityTransaction;

/**
 * Contains methods for managing application objects.
 */
public class ApplicationService extends AbstractService {

    public ApplicationService() {
        super();
    }

    /**
     * Returns Application object by id, assigned with calculated permissions.
     *
     * @param id Application id.
     * @return
     */
    public Application getApplicationWithPermissions(String id) {
        Application app = getApplication(id);
        if (app != null) {
            // Set application permissions
            app.setPermissions(getApplicationPersmissions(app));
        }
        return app;
    }

    /**
     * Returns Application object by id.
     *
     * @param id Application id.
     * @return
     */
    public Application getApplication(String id) {
        return getById(Application.class, id, false);
    }

    /**
     * Returns {@link ApplicationStatusChanger} object by id.
     *
     * @param id Application id.
     * @return
     */
    public ApplicationStatusChanger getApplicationStatusChanger(String id) {
        return getById(ApplicationStatusChanger.class, id, false);
    }

    /**
     * Returns Application number by id.
     *
     * @param id Application id.
     * @return
     */
    public ApplicationNumber getApplicationNumber(String id) {
        return getEM().find(ApplicationNumber.class, id);
    }

    /**
     * Assigns multiple applications to the user
     *
     * @param ids List of application ids
     * @param userName User name, who to assign the application
     */
    public void assignApplications(List<String> ids, String userName) {
        if (StringUtility.isEmpty(userName)) {
            return;
        }

        // Check user exist and active
        UserService userService = new UserService();
        User user = userService.getUser(userName);

        if (user == null || !user.isActive()) {
            throw new TrustException(MessagesKeys.ERR_USER_DONT_EXISTS_OR_ACTIVE, new Object[]{userName});
        }

        if (ids != null) {
            // Check assign role
            if (!isInRole(RolesConstants.ASSIGN_APPLICATIONS)) {
                throw new TrustException(MessagesKeys.ERR_NO_ASSIGN_ROLE);
            }

            EntityTransaction tx = null;
            try {
                tx = getEM().getTransaction();
                tx.begin();

                for (String id : ids) {
                    ApplicationBasic app = getById(ApplicationBasic.class, id, false);
                    if (app != null) {
                        // Check status
                        if (app.getCompleteDate() != null) {
                            throw new TrustException(MessagesKeys.ERR_APP_ARCHIVED, new Object[]{app.getAppNumber()});
                        }
                        // Do nothing if application already assigned to the same person
                        if (StringUtility.empty(app.getAssignee()).equalsIgnoreCase(userName)) {
                            continue;
                        }
                        // Check current user has right to re-assign
                        if (!StringUtility.empty(app.getAssignee()).equalsIgnoreCase(SharedData.getUserName()) && !isInRole(RolesConstants.RE_ASSIGN_APPLICATIONS)) {
                            throw new TrustException(MessagesKeys.ERR_NO_REASSIGN_ROLE);
                        }
                        // Assign user
                        app.setAssignee(userName);
                    }
                }

                // Commit changes
                tx.commit();
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                if (getEM().isOpen()) {
                    getEM().close();
                }
                throw e;
            }
        }
    }

    /**
     * Sets various application permissions
     */
    private ApplicationPermissions getApplicationPersmissions(Application app) {
        if (app == null) {
            return null;
        }
        ApplicationPermissions permissions = new ApplicationPermissions();
        boolean isAssignee = !StringUtility.isEmpty(app.getAssignee()) && StringUtility.empty(app.getAssignee()).equalsIgnoreCase(StringUtility.empty(SharedData.getUserName()));

        // Editing
        permissions.setCanEdit(canEdit(app));

        // Assigning/re-assigning
        permissions.setCanAssign(true);
        permissions.setCanReAssign(true);
        if (app.getCompleteDate() != null) {
            permissions.setCanAssign(false);
            permissions.setCanReAssign(false);
        }
        if (!isInRole(RolesConstants.ASSIGN_APPLICATIONS) || !isAssignee) {
            permissions.setCanAssign(false);
        }
        if (!isInRole(RolesConstants.RE_ASSIGN_APPLICATIONS) || StringUtility.isEmpty(app.getAssignee())) {
            permissions.setCanReAssign(false);
        }

        // Approve/reject
        permissions.setCanApprove(true);
        if (!app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            permissions.setCanApprove(false);
        }
        if (!isInRole(RolesConstants.APPROVE_TRANSACTIONS) || !isAssignee) {
            permissions.setCanApprove(false);
        }
        permissions.setCanReject(permissions.isCanApprove());

        // Withdraw
        permissions.setCanWithdraw(true);
        if (!app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            permissions.setCanWithdraw(false);
        }
        if (!isInRole(RolesConstants.WITHDRAW_APPLICATIONS) || !isAssignee) {
            permissions.setCanWithdraw(false);
        }

        // Complete
        permissions.setCanComplete(true);
        if (app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING) || app.getCompleteDate() != null) {
            permissions.setCanComplete(false);
        }
        if (!isInRole(RolesConstants.MANAGE_APPLICATIONS) || !isAssignee) {
            permissions.setCanComplete(false);
        }

        // Can draw parcel
        AppType appType = null;
        String transCode = "";

        if (!StringUtility.isEmpty(app.getAppTypeCode())) {
            appType = getById(AppType.class, app.getAppTypeCode(), false);
            if (appType != null) {
                transCode = StringUtility.empty(appType.getTransactionTypeCode());
            }
        }

        permissions.setCanDrawParcel(false);
        if (isAssignee && isInRole(RolesConstants.MANAGE_PARCELS) && isPending(app)
                && transCode.equalsIgnoreCase(TransactionType.FIRST_REGISTRATION)) {
            permissions.setCanDrawParcel(true);
        }

        // Can register rights
        permissions.setCanRegisterRight(false);
        if (!StringUtility.isEmpty(app.getId()) && isAssignee && isInRole(RolesConstants.MANAGE_RIGHTS) && isPending(app)
                && !transCode.equalsIgnoreCase(TransactionType.SURRENDER) && !transCode.equalsIgnoreCase(TransactionType.TERMINATION)) {
            if (!transCode.equalsIgnoreCase(TransactionType.FIRST_REGISTRATION) && app.getProperties() != null && app.getProperties().size() > 0) {
                permissions.setCanRegisterRight(true);
            } else if (transCode.equalsIgnoreCase(TransactionType.FIRST_REGISTRATION)) {
                // Check for created parcels, needed for right 
                PropertyService propService = new PropertyService();
                List<Parcel> parcels = propService.getParcelsByApplicationId(app.getId());
                if (parcels != null && parcels.size() > 0) {
                    permissions.setCanRegisterRight(true);
                }
            }
        }

        return permissions;
    }

    private boolean isPending(Application app) {
        return !StringUtility.isEmpty(app.getStatusCode()) && app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING);
    }

    private boolean canEdit(Application app) {
        if (!StringUtility.isEmpty(app.getStatusCode()) && !app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            return false;
        }
        if (!isInRole(RolesConstants.MANAGE_APPLICATIONS)) {
            return false;
        }
        if (!StringUtility.isEmpty(app.getAssignee()) && !app.getAssignee().equalsIgnoreCase(StringUtility.empty(SharedData.getUserName()))) {
            return false;
        }
        return true;
    }

    /**
     * Saves application and returns saved object back.
     *
     * @param app Application to save
     * @param langCode Language code
     * @return
     */
    public Application saveApplication(Application app, String langCode) {
        // Try to get application first
        boolean isNew = true;
        if (!StringUtility.isEmpty(app.getId())) {
            Application dbApp = getApplication(app.getId());
            if (dbApp != null) {
                isNew = false;
                // Check it can be edited
                if (!canEdit(dbApp)) {
                    throw new TrustException(MessagesKeys.ERR_APP_READ_ONLY);
                }
            }
        }

        // Assign id and user if new application
        if (StringUtility.isEmpty(app.getId())) {
            app.setId(UUID.randomUUID().toString());
        }
        if (isNew) {
            app.setAssignee(SharedData.getUserName());
        }

        // Validate
        validate(app, langCode);

        // Assign app id and party id to sublists
        if (app.getDocuments() != null) {
            for (ApplicationDocument appDoc : app.getDocuments()) {
                appDoc.setAppId(app.getId());
                // Assign id if missing
                if (StringUtility.isEmpty(appDoc.getDocument().getId())) {
                    appDoc.getDocument().setId(UUID.randomUUID().toString());
                }
                appDoc.setDocumentId(appDoc.getDocument().getId());
            }
        }
        if (app.getApplicants() != null) {
            for (ApplicationParty applicant : app.getApplicants()) {
                applicant.setAppId(app.getId());
                if (applicant.getParty() != null) {
                    // Assign id if missing
                    if (StringUtility.isEmpty(applicant.getParty().getId())) {
                        applicant.getParty().setId(UUID.randomUUID().toString());
                    }
                    applicant.setPartyId(applicant.getParty().getId());
                    // Update documents
                    updatePartyDocuments(applicant.getParty());
                }
            }
        }

        // Assign app id to properties
        if (app.getProperties() != null) {
            for (ApplicationProperty appProp : app.getProperties()) {
                appProp.setAppId(app.getId());
            }
        }

        // Save
        return save(app, true);
    }

    private void updatePartyDocuments(Party party) {
        if (party != null && party.getDocuments() != null) {
            for (PartyDocument partyDoc : party.getDocuments()) {
                partyDoc.setPartyId(party.getId());
                if (partyDoc.getDocument() != null) {
                    // Assign id if missing
                    if (StringUtility.isEmpty(partyDoc.getDocument().getId())) {
                        partyDoc.getDocument().setId(UUID.randomUUID().toString());
                    }
                    partyDoc.setDocumentId(partyDoc.getDocument().getId());
                }
            }
        }
    }

    private void validate(Application app, String langCode) {
        if (app == null) {
            return;
        }

        // Can be edited
        if (!canEdit(app)) {
            throw new TrustException(MessagesKeys.ERR_APP_READ_ONLY);
        }

        // Check app type
        if (StringUtility.isEmpty(app.getAppTypeCode())) {
            throw new TrustException(MessagesKeys.ERR_APP_TYPE_CODE_EMPTY);
        }

        // Check CCRO attached for transactions other than new registration
        RefDataService refService = new RefDataService();
        AppType appTye = refService.getRefDataRecord(AppType.class, app.getAppTypeCode(), langCode);

        if (!appTye.getTransactionTypeCode().equalsIgnoreCase(TransactionType.FIRST_REGISTRATION)
                && (app.getProperties() == null || app.getProperties().size() < 1)) {
            throw new TrustException(MessagesKeys.ERR_APP_CCRO_EMPTY);
        }

        SearchService searchService = new SearchService();

        if (app.getProperties() != null) {
            // Check properties not attached to other applications
            for (ApplicationProperty appProp : app.getProperties()) {
                List<ApplicationNumberSearchResult> appNumbers = searchService.searchAppNumbersByProp(appProp.getPropId());
                if (appNumbers != null) {
                    for (ApplicationNumberSearchResult appNumber : appNumbers) {
                        if (!app.getId().equalsIgnoreCase(appNumber.getId()) && appNumber.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
                            PropertyCodeSearchResult propCode = searchService.searchPropCodeById(langCode, appProp.getPropId());
                            if (propCode != null) {
                                throw new TrustException(MessagesKeys.ERR_APP_PROP_IN_USE, new Object[]{propCode.getPropNumber(), appNumber.getAppNumber()});
                            } else {
                                throw new TrustException(MessagesKeys.ERR_APP_PROP_IN_USE, new Object[]{appProp.getPropId(), appNumber.getAppNumber()});
                            }
                        }
                    }
                }
                // Check properties to exist and not being pending
                PropertyCodeSearchResult propCode = searchService.searchPropCodeById(langCode, appProp.getPropId());
                if (!propCode.getStatusCode().equals(StatusCodeConstants.CURRENT)) {
                    throw new TrustException(MessagesKeys.ERR_APP_PROP_NOT_REGISTERED, new Object[]{propCode.getPropNumber()});
                }
            }
        }

        // Check attached properties are not removed if pending rights are created
        if (!appTye.getTransactionTypeCode().equalsIgnoreCase(TransactionType.FIRST_REGISTRATION)) {
            List<AffectedObjectSearchResult> affectedObjects = searchService.searchAffectedObjects(langCode, app.getId());
            if (affectedObjects != null) {
                for (AffectedObjectSearchResult obj : affectedObjects) {
                    if (obj.getObjectType().equals(AffectedObjectSearchResult.OBJECT_TYPE_PROPERTY)) {
                        boolean found = false;
                        for (ApplicationProperty appProp : app.getProperties()) {
                            if (appProp.getPropId().equals(obj.getId())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new TrustException(MessagesKeys.ERR_APP_PROP_CANNOT_REMOVE, new Object[]{obj.getLabel()});
                        }
                    }
                }
            }
        }

        // Check applicants
        if (app.getApplicants() == null || app.getApplicants().size() < 1) {
            throw new TrustException(MessagesKeys.ERR_APP_NO_PERSONS);
        }

        int countLe = 0;
        int countPersons = 0;
        PartyService partyService = new PartyService();

        for (ApplicationParty applicant : app.getApplicants()) {
            if (applicant.getParty() != null) {
                if (applicant.getParty().isIsPrivate()) {
                    countPersons += 1;
                    partyService.validatePerson(applicant.getParty(), langCode);
                } else {
                    countLe += 1;
                    partyService.validateLegalEntity(applicant.getParty(), langCode);
                }
            }
        }

        if (countLe > 0) {
            if (countLe > 1) {
                throw new TrustException(MessagesKeys.ERR_APP_ONE_LE_REQUIRED);
            }
            if (countPersons < 1) {
                throw new TrustException(MessagesKeys.ERR_APP_NO_REP);
            } else if (countPersons > 1) {
                throw new TrustException(MessagesKeys.ERR_APP_ONE_REP_REQUIRED);
            }
        }

        // Check documents
        if (app.getDocuments() != null) {
            DocumentService docService = new DocumentService();
            for (ApplicationDocument appDoc : app.getDocuments()) {
                if (appDoc.getDocument() != null) {
                    docService.validateDocument(appDoc.getDocument());
                }
            }
        }
    }

    public void approveApplication(String appId) {
        if (StringUtility.isEmpty(appId)) {
            return;
        }
        ApplicationStatusChanger app = getApplicationStatusChanger(appId);

        if (app == null) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_FOUND);
        }

        // Validate
        checkCanApprove(app);

        // Get objects for approval
        PropertyService propService = new PropertyService();

        List<ParcelStatusChanger> parcels = propService.getParcelStatusChangersByApp(app.getId());
        List<PropertyStatusChanger> props = propService.getPropertyStatusChangersByApp(app.getId());
        List<RrrStatusChanger> rrrs = propService.getRrrStatusChangersByApp(app.getId());
        List<RrrStatusChanger> rrrsForTermination = propService.getRrrStatusChangersByTerminationApp(app.getId());

        if ((parcels == null || parcels.size() < 1) && (props == null || props.size() < 1) && (rrrs == null || rrrs.size() < 1) && (rrrsForTermination == null || rrrsForTermination.size() < 1)) {
            throw new TrustException(MessagesKeys.ERR_APP_NO_CHANGES_IN_THE_SYSTEM);
        }

        // Approve
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();

            Date currentDate = Calendar.getInstance().getTime();

            // Approve parcels
            if (parcels != null && parcels.size() > 0) {
                for (ParcelStatusChanger parcel : parcels) {
                    if (parcel.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        parcel.setStatusCode(StatusCodeConstants.ACTIVE);
                    }
                }
            }

            // Approve properties
            if (props != null && props.size() > 0) {
                for (PropertyStatusChanger prop : props) {
                    if (prop.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        prop.setRegDate(currentDate);
                        prop.setStatusCode(StatusCodeConstants.CURRENT);
                    }
                }
            }

            // Approve rights
            if (rrrs != null && rrrs.size() > 0) {
                for (RrrStatusChanger rrr : rrrs) {
                    if (rrr.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        rrr.setStatusCode(StatusCodeConstants.CURRENT);
                        rrr.setRegDate(currentDate);

                        // If has parent right, make it historic
                        if (!StringUtility.isEmpty(rrr.getParentId())) {
                            RrrStatusChanger parentRrr = propService.getRrrStatusChangerById(rrr.getParentId());
                            if (parentRrr != null && parentRrr.getStatusCode().equals(StatusCodeConstants.CURRENT)) {
                                parentRrr.setTerminationApplicationId(null);
                                parentRrr.setEndApplicationId(app.getId());
                                parentRrr.setTerminationDate(currentDate);
                                parentRrr.setStatusCode(StatusCodeConstants.HISTORIC);
                            }
                        }
                    }
                }
            }

            // Terminate rights
            if (rrrsForTermination != null && rrrsForTermination.size() > 0) {
                for (RrrStatusChanger rrr : rrrsForTermination) {
                    if (rrr.getStatusCode().equals(StatusCodeConstants.CURRENT)) {
                        rrr.setTerminationApplicationId(null);
                        rrr.setEndApplicationId(app.getId());
                        rrr.setTerminationDate(currentDate);
                        rrr.setStatusCode(StatusCodeConstants.HISTORIC);
                    }
                }
            }

            // Finally approve application
            app.setApproveRejectDate(currentDate);
            app.setStatusCode(StatusCodeConstants.APPROVED);

            // Commit changes
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }
    
    /**
     * Rejects application
     * @param appId Application id
     * @param reason Rejection reason
     * @returns
     */
    public void rejectApplication(String appId, String reason) {
        rejectWithdrawApplication(appId, reason, true);
    }
    
    /**
     * Withdraws application
     * @param appId Application id
     * @param reason Withdrawal reason
     * @returns
     */
    public void withdrawApplication(String appId, String reason) {
        rejectWithdrawApplication(appId, reason, false);
    }

    private void rejectWithdrawApplication(String appId, String reason, boolean isReject) {
        if (StringUtility.isEmpty(appId)) {
            return;
        }
        ApplicationStatusChanger app = getApplicationStatusChanger(appId);

        if (app == null) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_FOUND);
        }

        // Validate
        if (isReject) {
            checkCanApprove(app);
        } else {
            checkCanWithDraw(app);
        }

        // Reject/withdraw
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();

            PropertyService propService = new PropertyService();
            Date currentDate = Calendar.getInstance().getTime();

            // Clear rights for termination
            List<RrrStatusChanger> rrrsForTermination = propService.getRrrStatusChangersByTerminationApp(app.getId());
            if (rrrsForTermination != null && rrrsForTermination.size() > 0) {
                for (RrrStatusChanger rrr : rrrsForTermination) {
                    rrr.setTerminationApplicationId(null);
                }
            }

            // Remove pending rights
            List<RrrStatusChanger> rrrs = propService.getRrrStatusChangersByApp(app.getId());
            if (rrrs != null && rrrs.size() > 0) {
                for (RrrStatusChanger rrr : rrrs) {
                    if (rrr.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        getEM().remove(rrr);
                    }
                }
            }
            
            // Delete properties created by application
            List<PropertyStatusChanger> props = propService.getPropertyStatusChangersByApp(app.getId());
            if (props != null && props.size() > 0) {
                for (PropertyStatusChanger prop : props) {
                    if (prop.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        getEM().remove(prop);
                    }
                }
            }          

            // Remove parcels created by application
            List<ParcelStatusChanger> parcels = propService.getParcelStatusChangersByApp(app.getId());
            if (parcels != null && parcels.size() > 0) {
                for (ParcelStatusChanger parcel : parcels) {
                    if (parcel.getStatusCode().equals(StatusCodeConstants.PENDING)) {
                        getEM().remove(parcel);
                    }
                }
            }

            // Finally reject/withdraw application
            if (isReject) {
                app.setApproveRejectDate(currentDate);
                app.setRejectReason(reason);
                app.setStatusCode(StatusCodeConstants.REJECTED);
            } else {
                app.setWithdrawDate(currentDate);
                app.setWithdrawReason(reason);
                app.setStatusCode(StatusCodeConstants.WITHDRAWN);
            }

            // Commit changes
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }

    public void completeApplication(String appId) {
        if (StringUtility.isEmpty(appId)) {
            return;
        }
        ApplicationStatusChanger app = getApplicationStatusChanger(appId);

        if (app == null) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_FOUND);
        }

        // Validate
        checkCanComplete(app);

        app.setAssignee(null);
        app.setAssignedOn(null);
        app.setCompleteDate(Calendar.getInstance().getTime());
        save(app, false);
    }

    private void checkCanComplete(ApplicationStatusChanger app) {
        if (!isInRole(RolesConstants.MANAGE_APPLICATIONS)) {
            throw new TrustException(MessagesKeys.ERR_NO_MANAGE_APP_ROLE);
        }
        if (StringUtility.isEmpty(app.getStatusCode()) || app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            throw new TrustException(MessagesKeys.ERR_APP_PENDING);
        }
        if (!StringUtility.isEmpty(app.getAssignee()) && !app.getAssignee().equalsIgnoreCase(StringUtility.empty(SharedData.getUserName()))) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_ASSIGNED_TO_APPLICATION);
        }
    }

    private void checkCanWithDraw(ApplicationStatusChanger app) {
        if (!isInRole(RolesConstants.MANAGE_APPLICATIONS)) {
            throw new TrustException(MessagesKeys.ERR_NO_WITHDRAW_ROLE);
        }
        checkCanMakeActions(app);
    }

    private void checkCanApprove(ApplicationStatusChanger app) {
        if (!isInRole(RolesConstants.APPROVE_TRANSACTIONS)) {
            throw new TrustException(MessagesKeys.ERR_NO_APPROVE_REJECT_ROLE);
        }
        checkCanMakeActions(app);
    }

    private void checkCanMakeActions(ApplicationStatusChanger app) {
        if (StringUtility.isEmpty(app.getStatusCode()) || !app.getStatusCode().equalsIgnoreCase(StatusCodeConstants.PENDING)) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_PENDING);
        }
        if (!StringUtility.isEmpty(app.getAssignee()) && !app.getAssignee().equalsIgnoreCase(StringUtility.empty(SharedData.getUserName()))) {
            throw new TrustException(MessagesKeys.ERR_APP_NOT_ASSIGNED_TO_APPLICATION);
        }
    }
}

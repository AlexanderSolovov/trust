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
import com.dai.trust.models.party.Party;
import com.dai.trust.models.party.PartyDocument;
import com.dai.trust.models.property.Parcel;
import com.dai.trust.models.refdata.AppType;
import com.dai.trust.models.refdata.TransactionType;
import com.dai.trust.models.system.User;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.document.DocumentService;
import com.dai.trust.services.party.PartyService;
import com.dai.trust.services.property.PropertyService;
import com.dai.trust.services.refdata.RefDataService;
import com.dai.trust.services.system.UserService;
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
                && transCode.equalsIgnoreCase(TransactionType.OWNERSHIP_REGISTRATION)) {
            permissions.setCanDrawParcel(true);
        }

        // Can register rights
        permissions.setCanRegisterRight(false);
        if (!StringUtility.isEmpty(app.getId()) && isAssignee && isInRole(RolesConstants.MANAGE_RIGHTS) && isPending(app)
                && !transCode.equalsIgnoreCase(TransactionType.SURRENDER) && !transCode.equalsIgnoreCase(TransactionType.TERMINATION)) {
            if (!transCode.equalsIgnoreCase(TransactionType.OWNERSHIP_REGISTRATION) && app.getProperties() != null && app.getProperties().size() > 0) {
                permissions.setCanRegisterRight(true);
            } else if (transCode.equalsIgnoreCase(TransactionType.OWNERSHIP_REGISTRATION)) {
                // Check for created parcels, needed for right 
                PropertyService propService = new PropertyService();
                List<Parcel> parcels = propService.getParcelsByApplicationId(app.getId());
                if(parcels != null && parcels.size() > 0){
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

        // Validate
        validate(app, langCode);

        // Assign id and user if new application
        if (isNew) {
            app.setId(UUID.randomUUID().toString());
            app.setAssignee(SharedData.getUserName());
        }

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

        if (!appTye.getTransactionTypeCode().equalsIgnoreCase(TransactionType.OWNERSHIP_REGISTRATION)
                && (app.getProperties() == null || app.getProperties().size() < 1)) {
            throw new TrustException(MessagesKeys.ERR_APP_CCRO_EMPTY);
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
}

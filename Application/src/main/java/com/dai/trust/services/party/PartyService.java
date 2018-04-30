package com.dai.trust.services.party;

import com.dai.trust.common.DateUtility;
import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.application.ApplicationLog;
import com.dai.trust.models.document.DocumentLog;
import com.dai.trust.models.party.Party;
import com.dai.trust.models.party.PartyDocument;
import com.dai.trust.models.party.PartyLog;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.document.DocumentService;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Query;

/**
 * Contains methods for managing private persons and legal entities.
 */
public class PartyService extends AbstractService {

    public PartyService() {
        super();
    }

    /**
     * Returns Person object.
     *
     * @param id Person id.
     * @return
     */
    public Party getParty(String id) {
        Party p = getById(Party.class, id, false);
        if(p != null){
            p.setLogs(getPartyLogs(id));
        }
        return p;
    }

    /**
     * Extracts log records for party
     *
     * @param partyId Party id
     * @return
     */
    public List<PartyLog> getPartyLogs(String partyId) {
        if (partyId == null || partyId.equals("")) {
            return null;
        }

        Query q = getEM().createNativeQuery(
                "select (row_number() OVER ())  || '_' || :partyId as id, l.*, (case when au.username is null then l.action_user else au.first_name || ' ' || au.last_name end) as action_user_name\n"
                + "from (\n"
                + "select action_time, action_user from public.party where id = :partyId \n"
                + "union\n"
                + "select action_time, action_user from history.party where id = :partyId \n"
                + "union\n"
                + "select action_time, action_user from public.party_document where party_id = :partyId \n"
                + "union\n"
                + "select action_time, action_user from history.party_document where party_id = :partyId \n"
                + ") l left join appuser au on l.action_user = au.username \n"
                + "order by l.action_time", PartyLog.class);
        q.setParameter("partyId", partyId);
        List<PartyLog> logs = q.getResultList();
        
        // Process logs to remove events in the same transaction (with diffrence of 1 second)
        if(logs != null && logs.size() > 0){
            Iterator<PartyLog> iter = logs.iterator();
            Date actionTime = null;
            while(iter.hasNext()){
                PartyLog log = iter.next();
                if(actionTime != null && (log.getActionTime().getTime() - actionTime.getTime()) / 1000 <= 1){
                    actionTime = log.getActionTime();
                    // Delete action
                    iter.remove();
                } else {
                    actionTime = log.getActionTime();
                }
            }
        }
        
        return logs;
    }

    /**
     * Validates person
     *
     * @param person Person to validate
     * @param langCode Language code
     * @param strict Boolean value indicating whether to check for person to be
     * involved in registered rights and/or approved applications
     * @return
     */
    public boolean validatePerson(Party person, String langCode, boolean strict) {
        if (person == null) {
            return true;
        }

        // Make fields check
        if (StringUtility.isEmpty(person.getName1())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_FIRST_NAME_EMPTY);
        }
        if (StringUtility.isEmpty(person.getName2())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_LAST_NAME_EMPTY);
        }
        if (StringUtility.isEmpty(person.getName3())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_MIDDLE_NAME_EMPTY);
        }
        if (StringUtility.isEmpty(person.getIdTypeCode())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_ID_TYPE_EMPTY);
        }
        if (StringUtility.isEmpty(person.getIdNumber())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_ID_NUMBER_EMPTY);
        }
        if (person.getDob() == null) {
            throw new TrustException(MessagesKeys.ERR_PERSON_DOB_EMPTY);
        }
        if (StringUtility.isEmpty(person.getGenderCode())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_GENDER_EMPTY);
        }
        if (StringUtility.isEmpty(person.getCitizenshipCode())) {
            throw new TrustException(MessagesKeys.ERR_PERSON_CITIZENSHIP_EMPTY);
        }

        // Verify documents
        if (person.getDocuments() != null) {
            DocumentService docService = new DocumentService();
            MessageProvider msgProvider = new MessageProvider(langCode);
            try {
                for (PartyDocument pDoc : person.getDocuments()) {
                    docService.validateDocument(pDoc.getDocument(), strict);
                }
            } catch (TrustException e) {
                throw new TrustException(MessagesKeys.ERR_PERSON_DOC_ERROR,
                        new Object[]{person.getFullName(), msgProvider.getMessage(e.getMessage(), e.getMessageParameters())});
            }
        }

        // Get person from db
        Party dbPerson = null;
        if (strict && !StringUtility.isEmpty(person.getId())) {
            dbPerson = getById(Party.class, person.getId(), false);
        }

        // Person exists, check it's not involved in any approved applications or rights. If yes, throw error
        if (dbPerson != null) {
            // Check changes
            if (checkDocumentsChanges(person.getDocuments(), dbPerson.getDocuments())
                    || !StringUtility.empty(dbPerson.getAddress()).equals(StringUtility.empty(person.getAddress()))
                    || !StringUtility.empty(dbPerson.getName4()).equals(StringUtility.empty(person.getName4()))
                    || !StringUtility.empty(dbPerson.getCitizenshipCode()).equals(StringUtility.empty(person.getCitizenshipCode()))
                    || !StringUtility.empty(dbPerson.getName1()).equals(StringUtility.empty(person.getName1()))
                    || !StringUtility.empty(dbPerson.getGenderCode()).equals(StringUtility.empty(person.getGenderCode()))
                    || !StringUtility.empty(dbPerson.getIdNumber()).equals(StringUtility.empty(person.getIdNumber()))
                    || !StringUtility.empty(dbPerson.getIdTypeCode()).equals(StringUtility.empty(person.getIdTypeCode()))
                    || !StringUtility.empty(dbPerson.getName2()).equals(StringUtility.empty(person.getName2()))
                    || !StringUtility.empty(dbPerson.getMaritalStatusCode()).equals(StringUtility.empty(person.getMaritalStatusCode()))
                    || !StringUtility.empty(dbPerson.getName3()).equals(StringUtility.empty(person.getName3()))
                    || !StringUtility.empty(dbPerson.getMobileNumber()).equals(StringUtility.empty(person.getMobileNumber()))
                    || !StringUtility.empty(dbPerson.getPersonPhotoId()).equals(StringUtility.empty(person.getPersonPhotoId()))
                    || !DateUtility.areEqual(dbPerson.getDob(), person.getDob())) {
                if (!dbPerson.isEditable()) {
                    throw new TrustException(MessagesKeys.ERR_PERSON_READ_ONLY, new Object[]{person.getFullName()});
                }
            }
        }
        return true;
    }

    /**
     * Validates legal entity
     *
     * @param legalEntity Legal entity to validate
     * @param langCode Language code
     * @param strict Boolean value indicating whether to check for person to be
     * involved in registered rights and/or approved applications
     * @return
     */
    public boolean validateLegalEntity(Party legalEntity, String langCode, boolean strict) {
        if (legalEntity == null) {
            return true;
        }

        // Make fields check
        if (StringUtility.isEmpty(legalEntity.getName1())) {
            throw new TrustException(MessagesKeys.ERR_LE_NAME_EMPTY);
        }
        if (StringUtility.isEmpty(legalEntity.getEntityTypeCode())) {
            throw new TrustException(MessagesKeys.ERR_LE_TYPE_EMPTY);
        }

        // Verify documents
        if (legalEntity.getDocuments() != null) {
            DocumentService docService = new DocumentService();
            MessageProvider msgProvider = new MessageProvider(langCode);
            try {
                for (PartyDocument pDoc : legalEntity.getDocuments()) {
                    docService.validateDocument(pDoc.getDocument(), strict);
                }
            } catch (TrustException e) {
                throw new TrustException(MessagesKeys.ERR_LE_DOC_ERROR,
                        new Object[]{legalEntity.getName1(), msgProvider.getMessage(e.getMessage(), e.getMessageParameters())});
            }
        }

        // Get legal entity from db
        Party dbLegalEntity = null;
        if (strict && !StringUtility.isEmpty(legalEntity.getId())) {
            dbLegalEntity = getById(Party.class, legalEntity.getId(), false);
        }

        // Person exists, check it's not involved in any approved applications or rights. If yes, throw error
        if (dbLegalEntity != null) {
            // Check changes
            if (checkDocumentsChanges(legalEntity.getDocuments(), dbLegalEntity.getDocuments())
                    || !StringUtility.empty(legalEntity.getAddress()).equals(StringUtility.empty(dbLegalEntity.getAddress()))
                    || !StringUtility.empty(legalEntity.getEntityTypeCode()).equals(StringUtility.empty(dbLegalEntity.getEntityTypeCode()))
                    || !StringUtility.empty(legalEntity.getMobileNumber()).equals(StringUtility.empty(dbLegalEntity.getMobileNumber()))
                    || !StringUtility.empty(legalEntity.getName1()).equals(StringUtility.empty(dbLegalEntity.getName1()))
                    || !StringUtility.empty(legalEntity.getIdNumber()).equals(StringUtility.empty(dbLegalEntity.getIdNumber()))
                    || !DateUtility.areEqual(legalEntity.getDob(), dbLegalEntity.getDob())) {
                if (!dbLegalEntity.isEditable()) {
                    throw new TrustException(MessagesKeys.ERR_LE_READ_ONLY, new Object[]{legalEntity.getName1()});
                }
            }
        }
        return true;
    }

    private boolean checkDocumentsChanges(List<PartyDocument> partyDocs, List<PartyDocument> dbPartyDocs) {
        if ((partyDocs == null && dbPartyDocs != null) || (partyDocs != null && dbPartyDocs == null)) {
            return true;
        }

        if (partyDocs == null && dbPartyDocs == null) {
            return false;
        }

        if (partyDocs.size() != dbPartyDocs.size()) {
            return true;
        }

        for (PartyDocument pDoc : partyDocs) {
            boolean matches = false;
            for (PartyDocument dbpDoc : dbPartyDocs) {
                if (StringUtility.empty(pDoc.getDocument().getId()).equalsIgnoreCase(StringUtility.empty(dbpDoc.getDocument().getId()))) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                return true;
            }
        }
        return false;
    }
}

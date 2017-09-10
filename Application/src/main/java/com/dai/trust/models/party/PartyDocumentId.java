package com.dai.trust.models.party;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class PartyDocumentId implements Serializable {
    private String partyId;
    private String documentId;
    
    @Override
    public int hashCode() {
        return ((partyId == null ? 0 : partyId.hashCode()) + (documentId == null ? 0 : documentId.hashCode()));
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PartyDocumentId) {
            PartyDocumentId other = (PartyDocumentId) object;
            return (StringUtility.empty(other.getDocumentId()).equals(StringUtility.empty(this.getDocumentId())))
                    && (StringUtility.empty(other.getPartyId()).equals(StringUtility.empty(this.getPartyId())));
        }
        return false;
    }
}

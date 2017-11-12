package com.dai.trust.models.property;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class RrrDocumentId implements Serializable {
    private String rrrId;
    private String documentId;
    
    @Override
    public int hashCode() {
        return ((rrrId == null ? 0 : rrrId.hashCode()) + (documentId == null ? 0 : documentId.hashCode()));
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RrrDocumentId) {
            RrrDocumentId other = (RrrDocumentId) object;
            return (StringUtility.empty(other.getDocumentId()).equals(StringUtility.empty(this.getDocumentId())))
                    && (StringUtility.empty(other.getRrrId()).equals(StringUtility.empty(this.getRrrId())));
        }
        return false;
    }
}

package com.dai.trust.models.application;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class ApplicationDocumentId implements Serializable {
    private String appId;
    private String documentId;
    
    @Override
    public int hashCode() {
        return ((appId == null ? 0 : appId.hashCode()) + (documentId == null ? 0 : documentId.hashCode()));
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ApplicationDocumentId) {
            ApplicationDocumentId other = (ApplicationDocumentId) object;
            return (StringUtility.empty(other.getDocumentId()).equals(StringUtility.empty(this.getDocumentId())))
                    && (StringUtility.empty(other.getAppId()).equals(StringUtility.empty(this.getAppId())));
        }
        return false;
    }
}

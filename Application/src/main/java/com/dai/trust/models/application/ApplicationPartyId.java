package com.dai.trust.models.application;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class ApplicationPartyId implements Serializable {
    private String partyId;
    private String appId;
    
    @Override
    public int hashCode() {
        return ((partyId == null ? 0 : partyId.hashCode()) + (appId == null ? 0 : appId.hashCode()));
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ApplicationPartyId) {
            ApplicationPartyId other = (ApplicationPartyId) object;
            return (StringUtility.empty(other.getAppId()).equals(StringUtility.empty(this.getAppId())))
                    && (StringUtility.empty(other.getPartyId()).equals(StringUtility.empty(this.getPartyId())));
        }
        return false;
    }
}

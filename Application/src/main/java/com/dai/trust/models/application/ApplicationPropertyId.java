package com.dai.trust.models.application;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class ApplicationPropertyId implements Serializable {
    private String propId;
    private String appId;
    
    @Override
    public int hashCode() {
        return ((propId == null ? 0 : propId.hashCode()) + (appId == null ? 0 : appId.hashCode()));
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ApplicationPropertyId) {
            ApplicationPropertyId other = (ApplicationPropertyId) object;
            return (StringUtility.empty(other.getAppId()).equals(StringUtility.empty(this.getAppId())))
                    && (StringUtility.empty(other.getPropId()).equals(StringUtility.empty(this.getPropId())));
        }
        return false;
    }
}

package com.dai.trust.models.property;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class RightholderId implements Serializable {
    private String partyId;
    private String rrrId;
    
    @Override
    public int hashCode() {
        return ((partyId == null ? 0 : partyId.hashCode()) + (rrrId == null ? 0 : rrrId.hashCode()));
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RightholderId) {
            RightholderId other = (RightholderId) object;
            return (StringUtility.empty(other.getRrrId()).equals(StringUtility.empty(this.getRrrId())))
                    && (StringUtility.empty(other.getPartyId()).equals(StringUtility.empty(this.getPartyId())));
        }
        return false;
    }
}

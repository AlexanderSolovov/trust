package com.dai.trust.models.refdata;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;

public class AppTypeRightTypeId implements Serializable {
    private String appTypeCode;
    private String rightTypeCode;

    public String getAppTypeCode() {
        return appTypeCode;
    }

    public void setAppTypeCode(String appTypeCode) {
        this.appTypeCode = appTypeCode;
    }

    public String getRightTypeCode() {
        return rightTypeCode;
    }

    public void setRightTypeCode(String rightTypeCode) {
        this.rightTypeCode = rightTypeCode;
    }
    
    @Override
    public int hashCode() {
        return ((appTypeCode == null ? 0 : appTypeCode.hashCode()) + (rightTypeCode == null ? 0 : rightTypeCode.hashCode()));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AppTypeRightTypeId) {
            AppTypeRightTypeId other = (AppTypeRightTypeId) object;
            return (StringUtility.empty(other.getRightTypeCode()).equals(StringUtility.empty(this.getRightTypeCode())))
                    && (StringUtility.empty(other.getAppTypeCode()).equals(StringUtility.empty(this.getAppTypeCode())));
        }
        return false;
    }
}

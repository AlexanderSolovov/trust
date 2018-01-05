package com.dai.trust.models.application;

import java.io.Serializable;

/**
 * POJO object holding various permissions for the application
 */
public class ApplicationPermissions implements Serializable {
    private boolean canEdit;
    private boolean canApprove;
    private boolean canWithdraw;
    private boolean canReject;
    private boolean canAssign;
    private boolean canReAssign;
    private boolean canDrawParcel;
    private boolean canRegisterRight;
    private boolean canComplete;
    
    public ApplicationPermissions(){
    }

    public boolean isCanComplete() {
        return canComplete;
    }

    public void setCanComplete(boolean canComplete) {
        this.canComplete = canComplete;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanApprove() {
        return canApprove;
    }

    public void setCanApprove(boolean canApprove) {
        this.canApprove = canApprove;
    }

    public boolean isCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public boolean isCanReject() {
        return canReject;
    }

    public void setCanReject(boolean canReject) {
        this.canReject = canReject;
    }

    public boolean isCanAssign() {
        return canAssign;
    }

    public void setCanAssign(boolean canAssign) {
        this.canAssign = canAssign;
    }

    public boolean isCanReAssign() {
        return canReAssign;
    }

    public void setCanReAssign(boolean canReAssign) {
        this.canReAssign = canReAssign;
    }

    public boolean isCanDrawParcel() {
        return canDrawParcel;
    }

    public void setCanDrawParcel(boolean canDrawParcel) {
        this.canDrawParcel = canDrawParcel;
    }

    public boolean isCanRegisterRight() {
        return canRegisterRight;
    }

    public void setCanRegisterRight(boolean canRegisterRight) {
        this.canRegisterRight = canRegisterRight;
    }
}

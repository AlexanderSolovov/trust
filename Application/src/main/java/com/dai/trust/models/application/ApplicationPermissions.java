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
    private boolean canTriggerAction;
    
    public ApplicationPermissions(){
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

    public boolean isCanTriggerAction() {
        return canTriggerAction;
    }

    public void setCanTriggerAction(boolean canTriggerAction) {
        this.canTriggerAction = canTriggerAction;
    }
}

package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.ArrayList;
import java.util.List;

public class SetupWizard extends Gate {
    private final List<Action> mExceptions;
    private final DeviceProvisionedController mProvisionedController;
    private final DeviceProvisionedListener mProvisionedListener = new DeviceProvisionedListener() {
        public void onDeviceProvisionedChanged() {
            updateSetupComplete();
        }

        public void onUserSetupChanged() {
            updateSetupComplete();
        }

        private void updateSetupComplete() {
            boolean access$000 = SetupWizard.this.isSetupComplete();
            if (access$000 != SetupWizard.this.mSetupComplete) {
                SetupWizard.this.mSetupComplete = access$000;
                SetupWizard.this.notifyListener();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mSetupComplete;

    public SetupWizard(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        this.mProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mSetupComplete = isSetupComplete();
        this.mProvisionedController.addCallback(this.mProvisionedListener);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mProvisionedController.removeCallback(this.mProvisionedListener);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        return !this.mSetupComplete;
    }

    /* access modifiers changed from: private */
    public boolean isSetupComplete() {
        return this.mProvisionedController.isDeviceProvisioned() && this.mProvisionedController.isCurrentUserSetup();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isDeviceProvisioned -> ");
        sb.append(this.mProvisionedController.isDeviceProvisioned());
        sb.append("; isCurrentUserSetup -> ");
        sb.append(this.mProvisionedController.isCurrentUserSetup());
        sb.append("]");
        return sb.toString();
    }
}

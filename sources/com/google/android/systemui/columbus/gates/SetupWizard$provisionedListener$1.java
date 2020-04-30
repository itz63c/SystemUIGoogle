package com.google.android.systemui.columbus.gates;

import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;

/* compiled from: SetupWizard.kt */
public final class SetupWizard$provisionedListener$1 implements DeviceProvisionedListener {
    final /* synthetic */ SetupWizard this$0;

    SetupWizard$provisionedListener$1(SetupWizard setupWizard) {
        this.this$0 = setupWizard;
    }

    public void onDeviceProvisionedChanged() {
        updateSetupComplete();
    }

    public void onUserSetupChanged() {
        updateSetupComplete();
    }

    private final void updateSetupComplete() {
        boolean access$isSetupComplete = this.this$0.isSetupComplete();
        if (access$isSetupComplete != this.this$0.setupComplete) {
            this.this$0.setupComplete = access$isSetupComplete;
            this.this$0.notifyListener();
        }
    }
}

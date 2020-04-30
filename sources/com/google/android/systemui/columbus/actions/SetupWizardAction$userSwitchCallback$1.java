package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.UserManager;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

/* compiled from: SetupWizardAction.kt */
public final class SetupWizardAction$userSwitchCallback$1 extends KeyguardUpdateMonitorCallback {
    final /* synthetic */ Context $context;
    final /* synthetic */ SetupWizardAction this$0;

    SetupWizardAction$userSwitchCallback$1(SetupWizardAction setupWizardAction, Context context) {
        this.this$0 = setupWizardAction;
        this.$context = context;
    }

    public void onUserSwitching(int i) {
        this.this$0.deviceInDemoMode = UserManager.isDeviceInDemoMode(this.$context);
        this.this$0.notifyListener();
    }
}

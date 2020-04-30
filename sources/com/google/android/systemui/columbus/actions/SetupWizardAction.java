package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SetupWizardAction.kt */
public final class SetupWizardAction extends Action {
    /* access modifiers changed from: private */
    public boolean deviceInDemoMode;
    /* access modifiers changed from: private */
    public final KeyguardDeferredSetup keyguardDeferredSetupGate;
    private final SetupWizardAction$keyguardDeferredSetupListener$1 keyguardDeferredSetupListener = new SetupWizardAction$keyguardDeferredSetupListener$1(this);
    private final SettingsAction settingsAction;
    private final StatusBar statusBar;
    /* access modifiers changed from: private */
    public boolean userCompletedSuw = this.keyguardDeferredSetupGate.isSuwComplete();
    private final UserSelectedAction userSelectedAction;
    private final SetupWizardAction$userSwitchCallback$1 userSwitchCallback;

    public SetupWizardAction(Context context, SettingsAction settingsAction2, UserSelectedAction userSelectedAction2, KeyguardDeferredSetup keyguardDeferredSetup, StatusBar statusBar2, KeyguardUpdateMonitor keyguardUpdateMonitor) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(settingsAction2, "settingsAction");
        Intrinsics.checkParameterIsNotNull(userSelectedAction2, "userSelectedAction");
        Intrinsics.checkParameterIsNotNull(keyguardDeferredSetup, "keyguardDeferredSetupGate");
        Intrinsics.checkParameterIsNotNull(statusBar2, "statusBar");
        Intrinsics.checkParameterIsNotNull(keyguardUpdateMonitor, "keyguardUpdateMonitor");
        super(context, null);
        this.settingsAction = settingsAction2;
        this.userSelectedAction = userSelectedAction2;
        this.keyguardDeferredSetupGate = keyguardDeferredSetup;
        this.statusBar = statusBar2;
        this.userSwitchCallback = new SetupWizardAction$userSwitchCallback$1(this, context);
        keyguardUpdateMonitor.registerCallback(this.userSwitchCallback);
        this.keyguardDeferredSetupGate.activate();
        this.keyguardDeferredSetupGate.setListener(this.keyguardDeferredSetupListener);
    }

    public boolean isAvailable() {
        return !this.deviceInDemoMode && this.userSelectedAction.isAssistant() && this.userSelectedAction.isAvailable() && !this.userCompletedSuw && !this.settingsAction.isAvailable();
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        updateFeedbackEffects(i, detectionProperties);
    }

    public void updateFeedbackEffects(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            this.statusBar.collapseShade();
            if (!this.userCompletedSuw && !this.settingsAction.isAvailable()) {
                Intent intent = new Intent();
                intent.setAction("com.google.android.settings.COLUMBUS_GESTURE_TRAINING");
                intent.setPackage("com.android.settings");
                intent.setFlags(268468224);
                getContext().startActivityAsUser(intent, UserHandle.of(-2));
            }
        }
        super.updateFeedbackEffects(i, detectionProperties);
        this.userSelectedAction.updateFeedbackEffects(i, detectionProperties);
    }
}

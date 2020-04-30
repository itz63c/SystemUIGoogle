package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.Collections;

public class SetupWizardAction extends Action {
    /* access modifiers changed from: private */
    public boolean mDeviceInDemoMode;
    private final KeyguardDeferredSetup mKeyguardDeferredSetupGate;
    private final Listener mKeyguardDeferredSetupListener;
    private final LaunchOpa mLaunchOpa;
    private final SettingsAction mSettingsAction;
    private final String mSettingsPackageName;
    private final StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public boolean mUserCompletedSuw;
    private final KeyguardUpdateMonitorCallback mUserSwitchCallback;

    public static class Builder {
        private final Context mContext;
        private LaunchOpa mLaunchOpa;
        private SettingsAction mSettingsAction;
        private final StatusBar mStatusBar;

        public Builder(Context context, StatusBar statusBar) {
            this.mContext = context;
            this.mStatusBar = statusBar;
        }

        public Builder setSettingsAction(SettingsAction settingsAction) {
            this.mSettingsAction = settingsAction;
            return this;
        }

        public Builder setLaunchOpa(LaunchOpa launchOpa) {
            this.mLaunchOpa = launchOpa;
            return this;
        }

        public SetupWizardAction build() {
            SetupWizardAction setupWizardAction = new SetupWizardAction(this.mContext, this.mSettingsAction, this.mLaunchOpa, this.mStatusBar);
            return setupWizardAction;
        }
    }

    private SetupWizardAction(Context context, SettingsAction settingsAction, LaunchOpa launchOpa, StatusBar statusBar) {
        super(context, null);
        this.mUserSwitchCallback = new KeyguardUpdateMonitorCallback() {
            public void onUserSwitching(int i) {
                SetupWizardAction setupWizardAction = SetupWizardAction.this;
                setupWizardAction.mDeviceInDemoMode = UserManager.isDeviceInDemoMode(setupWizardAction.getContext());
                SetupWizardAction.this.notifyListener();
            }
        };
        this.mKeyguardDeferredSetupListener = new Listener() {
            public void onGateChanged(Gate gate) {
                SetupWizardAction.this.mUserCompletedSuw = ((KeyguardDeferredSetup) gate).isSuwComplete();
                SetupWizardAction.this.notifyListener();
            }
        };
        this.mSettingsPackageName = context.getResources().getString(C2017R$string.settings_app_package_name);
        this.mSettingsAction = settingsAction;
        this.mLaunchOpa = launchOpa;
        this.mStatusBar = statusBar;
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mUserSwitchCallback);
        KeyguardDeferredSetup keyguardDeferredSetup = new KeyguardDeferredSetup(context, Collections.emptyList());
        this.mKeyguardDeferredSetupGate = keyguardDeferredSetup;
        keyguardDeferredSetup.activate();
        this.mKeyguardDeferredSetupGate.setListener(this.mKeyguardDeferredSetupListener);
        this.mUserCompletedSuw = this.mKeyguardDeferredSetupGate.isSuwComplete();
    }

    public boolean isAvailable() {
        boolean z = false;
        if (this.mDeviceInDemoMode) {
            return false;
        }
        if (this.mLaunchOpa.isAvailable() && !this.mUserCompletedSuw && !this.mSettingsAction.isAvailable()) {
            z = true;
        }
        return z;
    }

    public void onProgress(float f, int i) {
        updateFeedbackEffects(f, i);
    }

    /* access modifiers changed from: protected */
    public void updateFeedbackEffects(float f, int i) {
        super.updateFeedbackEffects(f, i);
        this.mLaunchOpa.updateFeedbackEffects(f, i);
    }

    /* access modifiers changed from: protected */
    public void triggerFeedbackEffects(DetectionProperties detectionProperties) {
        super.triggerFeedbackEffects(detectionProperties);
        this.mLaunchOpa.triggerFeedbackEffects(detectionProperties);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        triggerFeedbackEffects(detectionProperties);
        if (!this.mUserCompletedSuw && !this.mSettingsAction.isAvailable()) {
            Intent intent = new Intent();
            intent.setAction("com.google.android.settings.ASSIST_GESTURE_TRAINING");
            intent.setPackage(this.mSettingsPackageName);
            intent.setFlags(268468224);
            getContext().startActivityAsUser(intent, UserHandle.of(-2));
        }
    }
}

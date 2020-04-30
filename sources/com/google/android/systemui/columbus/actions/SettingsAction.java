package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SettingsAction.kt */
public final class SettingsAction extends ServiceAction {
    private final StatusBar statusBar;
    private final UserSelectedAction userSelectedAction;

    public SettingsAction(Context context, UserSelectedAction userSelectedAction2, StatusBar statusBar2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(userSelectedAction2, "userSelectedAction");
        Intrinsics.checkParameterIsNotNull(statusBar2, "statusBar");
        super(context, null);
        this.userSelectedAction = userSelectedAction2;
        this.statusBar = statusBar2;
    }

    /* access modifiers changed from: protected */
    public boolean checkSupportedCaller() {
        return checkSupportedCaller("com.android.settings");
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            this.statusBar.collapseShade();
        }
        super.onProgress(i, detectionProperties);
    }

    /* access modifiers changed from: protected */
    public void triggerAction() {
        if (this.userSelectedAction.isAvailable()) {
            this.userSelectedAction.onTrigger();
        }
    }
}

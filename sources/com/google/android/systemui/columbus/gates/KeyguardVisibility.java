package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardVisibility.kt */
public class KeyguardVisibility extends Gate {
    private final KeyguardVisibility$keyguardMonitorCallback$1 keyguardMonitorCallback = new KeyguardVisibility$keyguardMonitorCallback$1(this);
    private final KeyguardStateController keyguardStateController;

    public KeyguardVisibility(Context context, KeyguardStateController keyguardStateController2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(keyguardStateController2, "keyguardStateController");
        super(context);
        this.keyguardStateController = keyguardStateController2;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.keyguardStateController.addCallback(this.keyguardMonitorCallback);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.keyguardStateController.removeCallback(this.keyguardMonitorCallback);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return isKeyguardShowing();
    }

    public final boolean isKeyguardShowing() {
        return this.keyguardStateController.isShowing();
    }

    public final boolean isKeyguardOccluded() {
        return this.keyguardStateController.isOccluded();
    }
}

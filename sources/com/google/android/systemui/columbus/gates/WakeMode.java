package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import dagger.Lazy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: WakeMode.kt */
public final class WakeMode extends PowerState {
    private final ColumbusContentObserver settingsObserver;
    private boolean wakeSettingEnabled = isWakeSettingEnabled();

    public WakeMode(Context context, Lazy<WakefulnessLifecycle> lazy, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(lazy, "wakefulnessLifecycle");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, lazy);
        Uri uriFor = Secure.getUriFor("assist_gesture_wake_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…IST_GESTURE_WAKE_ENABLED)");
        this.settingsObserver = factory.create(uriFor, new WakeMode$settingsObserver$1(this));
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.wakeSettingEnabled = isWakeSettingEnabled();
        this.settingsObserver.activate();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.settingsObserver.deactivate();
    }

    /* access modifiers changed from: private */
    public final void updateWakeSetting() {
        boolean isWakeSettingEnabled = isWakeSettingEnabled();
        if (isWakeSettingEnabled != this.wakeSettingEnabled) {
            this.wakeSettingEnabled = isWakeSettingEnabled;
            notifyListener();
        }
    }

    private final boolean isWakeSettingEnabled() {
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_wake_enabled", 1, -2) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        if (this.wakeSettingEnabled) {
            return false;
        }
        return super.isBlocked();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [wakeSettingEnabled -> ");
        sb.append(this.wakeSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}

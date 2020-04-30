package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.Optional;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UnpinNotifications.kt */
public final class UnpinNotifications extends Action {
    /* access modifiers changed from: private */
    public boolean hasPinnedHeadsUp;
    private final UnpinNotifications$headsUpChangedListener$1 headsUpChangedListener = new UnpinNotifications$headsUpChangedListener$1(this);
    private HeadsUpManager headsUpManager;
    private ColumbusContentObserver settingsObserver;
    private boolean silenceSettingEnabled;

    public UnpinNotifications(Optional<HeadsUpManager> optional, Context context, Factory factory) {
        Intrinsics.checkParameterIsNotNull(optional, "headsUpManagerOptional");
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        Uri uriFor = Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…E_SILENCE_ALERTS_ENABLED)");
        this.settingsObserver = factory.create(uriFor, new UnpinNotifications$settingsObserver$1(this));
        HeadsUpManager headsUpManager2 = (HeadsUpManager) optional.orElse(null);
        this.headsUpManager = headsUpManager2;
        if (headsUpManager2 != null) {
            updateHeadsUpListener();
            this.settingsObserver.activate();
            return;
        }
        Log.w("Columbus/UnpinNotifications", "No HeadsUpManager");
    }

    /* access modifiers changed from: private */
    public final void updateHeadsUpListener() {
        boolean z = true;
        boolean z2 = false;
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            z = false;
        }
        if (this.silenceSettingEnabled != z) {
            this.silenceSettingEnabled = z;
            if (z) {
                HeadsUpManager headsUpManager2 = this.headsUpManager;
                if (headsUpManager2 != null) {
                    z2 = headsUpManager2.hasPinnedHeadsUp();
                }
                this.hasPinnedHeadsUp = z2;
                HeadsUpManager headsUpManager3 = this.headsUpManager;
                if (headsUpManager3 != null) {
                    headsUpManager3.addListener(this.headsUpChangedListener);
                }
            } else {
                this.hasPinnedHeadsUp = false;
                HeadsUpManager headsUpManager4 = this.headsUpManager;
                if (headsUpManager4 != null) {
                    headsUpManager4.removeListener(this.headsUpChangedListener);
                }
            }
            notifyListener();
        }
    }

    public boolean isAvailable() {
        if (this.silenceSettingEnabled) {
            return this.hasPinnedHeadsUp;
        }
        return false;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        super.onProgress(i, detectionProperties);
        if (i == 3) {
            HeadsUpManager headsUpManager2 = this.headsUpManager;
            if (headsUpManager2 != null) {
                headsUpManager2.unpinAll(true);
            }
        }
    }
}

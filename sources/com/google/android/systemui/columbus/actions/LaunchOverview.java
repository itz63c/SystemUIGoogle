package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.recents.Recents;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LaunchOverview.kt */
public final class LaunchOverview extends Action {
    private final Recents recents;

    public boolean isAvailable() {
        return true;
    }

    public LaunchOverview(Context context, Recents recents2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(recents2, "recents");
        super(context, null);
        this.recents = recents2;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            onTrigger();
        }
    }

    public void onTrigger() {
        this.recents.toggleRecentApps();
    }
}

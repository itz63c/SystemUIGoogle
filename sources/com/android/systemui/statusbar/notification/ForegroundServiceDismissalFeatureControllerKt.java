package com.android.systemui.statusbar.notification;

import com.android.systemui.util.DeviceConfigProxy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ForegroundServiceDismissalFeatureController.kt */
public final class ForegroundServiceDismissalFeatureControllerKt {
    private static Boolean sIsEnabled;

    /* access modifiers changed from: private */
    public static final boolean isEnabled(DeviceConfigProxy deviceConfigProxy) {
        if (sIsEnabled == null) {
            sIsEnabled = Boolean.valueOf(deviceConfigProxy.getBoolean("systemui", "notifications_allow_fgs_dismissal", true));
        }
        Boolean bool = sIsEnabled;
        if (bool != null) {
            return bool.booleanValue();
        }
        Intrinsics.throwNpe();
        throw null;
    }
}

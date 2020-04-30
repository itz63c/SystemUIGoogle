package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.util.DeviceConfigProxy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ForegroundServiceDismissalFeatureController.kt */
public final class ForegroundServiceDismissalFeatureController {
    private final DeviceConfigProxy proxy;

    public ForegroundServiceDismissalFeatureController(DeviceConfigProxy deviceConfigProxy, Context context) {
        Intrinsics.checkParameterIsNotNull(deviceConfigProxy, "proxy");
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.proxy = deviceConfigProxy;
    }

    public final boolean isForegroundServiceDismissalEnabled() {
        return ForegroundServiceDismissalFeatureControllerKt.isEnabled(this.proxy);
    }
}

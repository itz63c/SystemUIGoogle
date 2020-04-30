package com.google.android.systemui.columbus.feedback;

import com.android.systemui.statusbar.NavigationBarController;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NavUndimEffect.kt */
public final class NavUndimEffect implements FeedbackEffect {
    private final NavigationBarController navBarController;

    public NavUndimEffect(NavigationBarController navigationBarController) {
        Intrinsics.checkParameterIsNotNull(navigationBarController, "navBarController");
        this.navBarController = navigationBarController;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        this.navBarController.touchAutoDim(0);
    }
}

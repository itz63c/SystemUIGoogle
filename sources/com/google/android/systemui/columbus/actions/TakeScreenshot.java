package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import com.android.internal.util.ScreenshotHelper;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TakeScreenshot.kt */
public final class TakeScreenshot extends Action {
    private final Handler handler;
    private final ScreenshotHelper screenshotHelper;

    public boolean isAvailable() {
        return true;
    }

    public TakeScreenshot(Context context, Handler handler2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler2, "handler");
        super(context, null);
        this.handler = handler2;
        this.screenshotHelper = new ScreenshotHelper(context);
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            onTrigger();
        }
    }

    public void onTrigger() {
        this.screenshotHelper.takeScreenshot(1, true, true, this.handler, null);
    }
}

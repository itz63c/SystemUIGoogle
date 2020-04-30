package com.android.systemui.util.magnetictarget;

import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.System;

/* compiled from: MagnetizedObject.kt */
public final class MagnetizedObject$hapticSettingObserver$1 extends ContentObserver {
    final /* synthetic */ MagnetizedObject this$0;

    MagnetizedObject$hapticSettingObserver$1(MagnetizedObject magnetizedObject, Handler handler) {
        this.this$0 = magnetizedObject;
        super(handler);
    }

    public void onChange(boolean z) {
        MagnetizedObject magnetizedObject = this.this$0;
        boolean z2 = false;
        if (System.getIntForUser(magnetizedObject.getContext().getContentResolver(), "haptic_feedback_enabled", 0, -2) != 0) {
            z2 = true;
        }
        magnetizedObject.systemHapticsEnabled = z2;
    }
}

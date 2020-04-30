package com.android.systemui.controls.controller;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$settingObserver$1 extends ContentObserver {
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$settingObserver$1(ControlsControllerImpl controlsControllerImpl, Handler handler) {
        this.this$0 = controlsControllerImpl;
        super(handler);
    }

    public void onChange(boolean z, Collection<? extends Uri> collection, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(collection, "uris");
        if (!this.this$0.userChanging && i2 == this.this$0.getCurrentUserId()) {
            ControlsControllerImpl controlsControllerImpl = this.this$0;
            boolean z2 = true;
            if (Secure.getIntForUser(controlsControllerImpl.getContentResolver(), "systemui.controls_available", 1, this.this$0.getCurrentUserId()) == 0) {
                z2 = false;
            }
            controlsControllerImpl.available = z2;
            ControlsControllerImpl controlsControllerImpl2 = this.this$0;
            controlsControllerImpl2.resetFavorites(controlsControllerImpl2.getAvailable());
        }
    }
}

package com.android.systemui.controls.management;

import android.util.Log;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;

/* compiled from: ControlsListingControllerImpl.kt */
final class ControlsListingControllerImpl$removeCallback$1 implements Runnable {
    final /* synthetic */ ControlsListingCallback $listener;
    final /* synthetic */ ControlsListingControllerImpl this$0;

    ControlsListingControllerImpl$removeCallback$1(ControlsListingControllerImpl controlsListingControllerImpl, ControlsListingCallback controlsListingCallback) {
        this.this$0 = controlsListingControllerImpl;
        this.$listener = controlsListingCallback;
    }

    public final void run() {
        Log.d("ControlsListingControllerImpl", "Unsubscribing callback");
        this.this$0.callbacks.remove(this.$listener);
    }
}

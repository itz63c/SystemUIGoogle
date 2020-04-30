package com.android.systemui.controls.controller;

import android.service.controls.IControlsSubscriber.Stub;
import android.util.Log;

/* compiled from: ControlsProviderLifecycleManager.kt */
final class ControlsProviderLifecycleManager$maybeBindAndLoadSuggested$1 implements Runnable {
    final /* synthetic */ Stub $subscriber;
    final /* synthetic */ ControlsProviderLifecycleManager this$0;

    ControlsProviderLifecycleManager$maybeBindAndLoadSuggested$1(ControlsProviderLifecycleManager controlsProviderLifecycleManager, Stub stub) {
        this.this$0 = controlsProviderLifecycleManager;
        this.$subscriber = stub;
    }

    public final void run() {
        String access$getTAG$p = this.this$0.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Timeout waiting onLoadSuggested for ");
        sb.append(this.this$0.getComponentName());
        Log.d(access$getTAG$p, sb.toString());
        this.$subscriber.onError(this.this$0.getToken(), "Timeout waiting onLoadSuggested");
        this.this$0.unbindService();
    }
}

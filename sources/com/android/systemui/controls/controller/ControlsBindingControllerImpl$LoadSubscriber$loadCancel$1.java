package com.android.systemui.controls.controller;

import android.util.Log;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* compiled from: ControlsBindingControllerImpl.kt */
final class ControlsBindingControllerImpl$LoadSubscriber$loadCancel$1 implements Runnable {
    final /* synthetic */ LoadSubscriber this$0;

    ControlsBindingControllerImpl$LoadSubscriber$loadCancel$1(LoadSubscriber loadSubscriber) {
        this.this$0 = loadSubscriber;
    }

    public final void run() {
        Log.d("ControlsBindingControllerImpl", "Cancel load requested");
        Function0 access$get_loadCancelInternal$p = this.this$0._loadCancelInternal;
        if (access$get_loadCancelInternal$p != null) {
            Unit unit = (Unit) access$get_loadCancelInternal$p.invoke();
        }
    }
}

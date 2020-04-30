package com.android.systemui.controls.controller;

import android.os.IBinder;
import android.service.controls.IControlsActionCallback.Stub;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsBindingControllerImpl.kt */
public final class ControlsBindingControllerImpl$actionCallbackService$1 extends Stub {
    final /* synthetic */ ControlsBindingControllerImpl this$0;

    ControlsBindingControllerImpl$actionCallbackService$1(ControlsBindingControllerImpl controlsBindingControllerImpl) {
        this.this$0 = controlsBindingControllerImpl;
    }

    public void accept(IBinder iBinder, String str, int i) {
        Intrinsics.checkParameterIsNotNull(iBinder, "token");
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        this.this$0.backgroundExecutor.execute(new OnActionResponseRunnable(this.this$0, iBinder, str, i));
    }
}

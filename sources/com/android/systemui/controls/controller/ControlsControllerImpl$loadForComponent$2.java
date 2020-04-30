package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import com.android.systemui.controls.controller.ControlsBindingController.LoadCallback;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$loadForComponent$2 implements LoadCallback {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ Consumer $dataCallback;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$loadForComponent$2(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, Consumer consumer) {
        this.this$0 = controlsControllerImpl;
        this.$componentName = componentName;
        this.$dataCallback = consumer;
    }

    public void accept(List<Control> list) {
        Intrinsics.checkParameterIsNotNull(list, "controls");
        this.this$0.loadCanceller = null;
        this.this$0.executor.execute(new ControlsControllerImpl$loadForComponent$2$accept$1(this, list));
    }

    public void error(String str) {
        Intrinsics.checkParameterIsNotNull(str, "message");
        this.this$0.loadCanceller = null;
        this.this$0.executor.execute(new ControlsControllerImpl$loadForComponent$2$error$1(this));
    }
}

package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.function.Consumer;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$loadForComponent$1 implements Runnable {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ Consumer $dataCallback;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$loadForComponent$1(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, Consumer consumer) {
        this.this$0 = controlsControllerImpl;
        this.$componentName = componentName;
        this.$dataCallback = consumer;
    }

    public final void run() {
        this.this$0.loadForComponent(this.$componentName, this.$dataCallback);
    }
}

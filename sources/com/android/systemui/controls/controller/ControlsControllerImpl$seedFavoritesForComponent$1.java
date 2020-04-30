package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.function.Consumer;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$seedFavoritesForComponent$1 implements Runnable {
    final /* synthetic */ Consumer $callback;
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$seedFavoritesForComponent$1(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, Consumer consumer) {
        this.this$0 = controlsControllerImpl;
        this.$componentName = componentName;
        this.$callback = consumer;
    }

    public final void run() {
        this.this$0.seedFavoritesForComponent(this.$componentName, this.$callback);
    }
}

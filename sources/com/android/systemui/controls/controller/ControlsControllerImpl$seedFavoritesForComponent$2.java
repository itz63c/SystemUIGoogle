package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import android.util.Log;
import com.android.systemui.controls.controller.ControlsBindingController.LoadCallback;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$seedFavoritesForComponent$2 implements LoadCallback {
    final /* synthetic */ Consumer $callback;
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$seedFavoritesForComponent$2(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, Consumer consumer) {
        this.this$0 = controlsControllerImpl;
        this.$componentName = componentName;
        this.$callback = consumer;
    }

    public void accept(List<Control> list) {
        Intrinsics.checkParameterIsNotNull(list, "controls");
        this.this$0.executor.execute(new ControlsControllerImpl$seedFavoritesForComponent$2$accept$1(this, list));
    }

    public void error(String str) {
        Intrinsics.checkParameterIsNotNull(str, "message");
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to seed favorites: ");
        sb.append(str);
        Log.e("ControlsControllerImpl", sb.toString());
        this.this$0.executor.execute(new ControlsControllerImpl$seedFavoritesForComponent$2$error$1(this));
    }
}

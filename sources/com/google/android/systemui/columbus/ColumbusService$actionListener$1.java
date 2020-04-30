package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.Action.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusService.kt */
public final class ColumbusService$actionListener$1 implements Listener {
    final /* synthetic */ ColumbusService this$0;

    ColumbusService$actionListener$1(ColumbusService columbusService) {
        this.this$0 = columbusService;
    }

    public void onActionAvailabilityChanged(Action action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        this.this$0.updateSensorListener();
    }
}

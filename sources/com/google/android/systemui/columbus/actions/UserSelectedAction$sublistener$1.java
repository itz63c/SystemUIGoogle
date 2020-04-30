package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.actions.Action.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UserSelectedAction.kt */
public final class UserSelectedAction$sublistener$1 implements Listener {
    final /* synthetic */ UserSelectedAction this$0;

    UserSelectedAction$sublistener$1(UserSelectedAction userSelectedAction) {
        this.this$0 = userSelectedAction;
    }

    public void onActionAvailabilityChanged(Action action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        if (Intrinsics.areEqual((Object) this.this$0.selectedAction, (Object) action)) {
            this.this$0.notifyListener();
        }
    }
}

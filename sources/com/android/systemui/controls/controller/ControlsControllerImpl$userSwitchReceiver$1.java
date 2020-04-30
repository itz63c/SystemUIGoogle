package com.android.systemui.controls.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$userSwitchReceiver$1 extends BroadcastReceiver {
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$userSwitchReceiver$1(ControlsControllerImpl controlsControllerImpl) {
        this.this$0 = controlsControllerImpl;
    }

    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        if (Intrinsics.areEqual((Object) intent.getAction(), (Object) "android.intent.action.USER_SWITCHED")) {
            this.this$0.userChanging = true;
            this.this$0.listingController.removeCallback(this.this$0.listingCallback);
            UserHandle of = UserHandle.of(intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()));
            if (Intrinsics.areEqual((Object) this.this$0.currentUser, (Object) of)) {
                this.this$0.userChanging = false;
                return;
            }
            ControlsControllerImpl controlsControllerImpl = this.this$0;
            Intrinsics.checkExpressionValueIsNotNull(of, "newUser");
            controlsControllerImpl.setValuesForUser(of);
        }
    }
}

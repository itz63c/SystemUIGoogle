package com.android.systemui.controls.management;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.CurrentUserTracker;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$currentUserTracker$1 extends CurrentUserTracker {
    private final int startingUser;
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$currentUserTracker$1(ControlsFavoritingActivity controlsFavoritingActivity, BroadcastDispatcher broadcastDispatcher, BroadcastDispatcher broadcastDispatcher2) {
        this.this$0 = controlsFavoritingActivity;
        super(broadcastDispatcher2);
        this.startingUser = controlsFavoritingActivity.controller.getCurrentUserId();
    }

    public void onUserSwitched(int i) {
        if (i != this.startingUser) {
            stopTracking();
            this.this$0.finish();
        }
    }
}

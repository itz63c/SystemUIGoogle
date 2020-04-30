package com.google.android.systemui.columbus.actions;

import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

/* compiled from: UnpinNotifications.kt */
public final class UnpinNotifications$headsUpChangedListener$1 implements OnHeadsUpChangedListener {
    final /* synthetic */ UnpinNotifications this$0;

    UnpinNotifications$headsUpChangedListener$1(UnpinNotifications unpinNotifications) {
        this.this$0 = unpinNotifications;
    }

    public void onHeadsUpPinnedModeChanged(boolean z) {
        if (this.this$0.hasPinnedHeadsUp != z) {
            this.this$0.hasPinnedHeadsUp = z;
            this.this$0.notifyListener();
        }
    }
}

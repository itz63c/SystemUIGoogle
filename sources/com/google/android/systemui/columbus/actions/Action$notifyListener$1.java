package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.actions.Action.Listener;

/* compiled from: Action.kt */
final class Action$notifyListener$1 implements Runnable {
    final /* synthetic */ Action this$0;

    Action$notifyListener$1(Action action) {
        this.this$0 = action;
    }

    public final void run() {
        Listener listener = this.this$0.getListener();
        if (listener != null) {
            listener.onActionAvailabilityChanged(this.this$0);
        }
    }
}

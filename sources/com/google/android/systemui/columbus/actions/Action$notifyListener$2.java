package com.google.android.systemui.columbus.actions;

/* compiled from: Action.kt */
final class Action$notifyListener$2 implements Runnable {
    final /* synthetic */ Action this$0;

    Action$notifyListener$2(Action action) {
        this.this$0 = action;
    }

    public final void run() {
        this.this$0.updateFeedbackEffects(0, null);
    }
}

package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.content.Intent;

public class DismissTimer extends DeskClockAction {
    /* access modifiers changed from: protected */
    public String getAlertAction() {
        return "com.google.android.deskclock.action.TIMER_ALERT";
    }

    /* access modifiers changed from: protected */
    public String getDoneAction() {
        return "com.google.android.deskclock.action.TIMER_DONE";
    }

    public DismissTimer(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public Intent createDismissIntent() {
        return new Intent("android.intent.action.DISMISS_TIMER");
    }
}

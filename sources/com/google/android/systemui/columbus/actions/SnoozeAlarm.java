package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.content.Intent;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SnoozeAlarm.kt */
public final class SnoozeAlarm extends DeskClockAction {
    /* access modifiers changed from: protected */
    public String getAlertAction() {
        return "com.google.android.deskclock.action.ALARM_ALERT";
    }

    /* access modifiers changed from: protected */
    public String getDoneAction() {
        return "com.google.android.deskclock.action.ALARM_DONE";
    }

    public SnoozeAlarm(Context context, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, factory);
    }

    /* access modifiers changed from: protected */
    public Intent createDismissIntent() {
        return new Intent("android.intent.action.SNOOZE_ALARM");
    }
}

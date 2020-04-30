package com.android.systemui.controls.controller;

import android.os.IBinder;
import android.service.controls.Control;
import android.util.Log;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: StatefulControlSubscriber.kt */
final class StatefulControlSubscriber$onNext$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ Control $control;
    final /* synthetic */ IBinder $token;
    final /* synthetic */ StatefulControlSubscriber this$0;

    StatefulControlSubscriber$onNext$1(StatefulControlSubscriber statefulControlSubscriber, IBinder iBinder, Control control) {
        this.this$0 = statefulControlSubscriber;
        this.$token = iBinder;
        this.$control = control;
        super(0);
    }

    public final void invoke() {
        if (!this.this$0.subscriptionOpen) {
            StringBuilder sb = new StringBuilder();
            sb.append("Refresh outside of window for token:");
            sb.append(this.$token);
            Log.w("StatefulControlSubscriber", sb.toString());
            return;
        }
        this.this$0.controller.refreshStatus(this.this$0.provider.getComponentName(), this.$control);
    }
}

package com.android.systemui.controls.controller;

import android.util.Log;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: StatefulControlSubscriber.kt */
final class StatefulControlSubscriber$onComplete$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ StatefulControlSubscriber this$0;

    StatefulControlSubscriber$onComplete$1(StatefulControlSubscriber statefulControlSubscriber) {
        this.this$0 = statefulControlSubscriber;
        super(0);
    }

    public final void invoke() {
        if (this.this$0.subscriptionOpen) {
            this.this$0.subscriptionOpen = false;
            StringBuilder sb = new StringBuilder();
            sb.append("onComplete receive from '");
            sb.append(this.this$0.provider.getComponentName());
            sb.append('\'');
            Log.i("StatefulControlSubscriber", sb.toString());
        }
    }
}

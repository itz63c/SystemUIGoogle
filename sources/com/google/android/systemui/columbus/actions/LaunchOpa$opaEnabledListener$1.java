package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.util.Log;
import com.google.android.systemui.assist.OpaEnabledListener;

/* compiled from: LaunchOpa.kt */
final class LaunchOpa$opaEnabledListener$1 implements OpaEnabledListener {
    final /* synthetic */ LaunchOpa this$0;

    LaunchOpa$opaEnabledListener$1(LaunchOpa launchOpa) {
        this.this$0 = launchOpa;
    }

    public final void onOpaEnabledReceived(Context context, boolean z, boolean z2, boolean z3) {
        boolean z4 = false;
        boolean z5 = z2 || this.this$0.enableForAnyAssistant;
        StringBuilder sb = new StringBuilder();
        sb.append("eligible: ");
        sb.append(z);
        sb.append(", supported: ");
        sb.append(z5);
        sb.append(", opa: ");
        sb.append(z3);
        Log.i("Columbus/LaunchOpa", sb.toString());
        if (z && z5 && z3) {
            z4 = true;
        }
        if (this.this$0.isOpaEnabled != z4) {
            this.this$0.isOpaEnabled = z4;
            this.this$0.notifyListener();
        }
    }
}

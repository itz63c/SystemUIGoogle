package com.android.systemui.controls.p004ui;

import android.app.ActivityOptions;
import android.app.ActivityView;
import android.app.ActivityView.StateCallback;
import android.content.ComponentName;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.DetailDialog$stateCallback$1 */
/* compiled from: DetailDialog.kt */
public final class DetailDialog$stateCallback$1 extends StateCallback {
    final /* synthetic */ DetailDialog this$0;

    public void onActivityViewDestroyed(ActivityView activityView) {
        Intrinsics.checkParameterIsNotNull(activityView, "view");
    }

    public void onTaskCreated(int i, ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
    }

    public void onTaskRemovalStarted(int i) {
    }

    DetailDialog$stateCallback$1(DetailDialog detailDialog) {
        this.this$0 = detailDialog;
    }

    public void onActivityViewReady(ActivityView activityView) {
        Intrinsics.checkParameterIsNotNull(activityView, "view");
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.addFlags(134217728);
        activityView.startActivity(this.this$0.getIntent(), intent, ActivityOptions.makeBasic());
    }
}

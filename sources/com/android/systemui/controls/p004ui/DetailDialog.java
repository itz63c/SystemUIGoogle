package com.android.systemui.controls.p004ui;

import android.app.ActivityView;
import android.app.ActivityView.StateCallback;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.DetailDialog */
/* compiled from: DetailDialog.kt */
public final class DetailDialog extends Dialog {
    private ActivityView activityView;
    private final PendingIntent intent;
    private final StateCallback stateCallback = new DetailDialog$stateCallback$1(this);

    public final PendingIntent getIntent() {
        return this.intent;
    }

    public DetailDialog(Context context, PendingIntent pendingIntent) {
        Intrinsics.checkParameterIsNotNull(context, "parentContext");
        Intrinsics.checkParameterIsNotNull(pendingIntent, "intent");
        super(context);
        this.intent = pendingIntent;
        Window window = getWindow();
        if (window != null) {
            setWindowParams(window);
        }
        setContentView(C2013R$layout.controls_detail_dialog);
        this.activityView = new ActivityView(getContext(), null, 0, false);
        ((ViewGroup) requireViewById(C2011R$id.controls_activity_view)).addView(this.activityView);
    }

    private final void setWindowParams(Window window) {
        window.requestFeature(1);
        window.getDecorView();
        window.getAttributes().systemUiVisibility = window.getAttributes().systemUiVisibility | 1024 | 256;
        window.setLayout(-1, -1);
        window.clearFlags(2);
        window.addFlags(16843008);
        window.setType(2020);
        window.getAttributes().setFitInsetsTypes(0);
    }

    public void show() {
        Window window = getWindow();
        LayoutParams attributes = window != null ? window.getAttributes() : null;
        if (attributes != null) {
            attributes.layoutInDisplayCutoutMode = 3;
        }
        Window window2 = getWindow();
        if (window2 != null) {
            window2.setAttributes(attributes);
        }
        this.activityView.setCallback(this.stateCallback);
        super.show();
    }

    public void dismiss() {
        this.activityView.release();
        super.dismiss();
    }
}

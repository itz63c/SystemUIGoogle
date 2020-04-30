package com.android.systemui.controls.management;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: ControlsProviderSelectorActivity.kt */
final class ControlsProviderSelectorActivity$onCreate$4 implements OnClickListener {
    final /* synthetic */ ControlsProviderSelectorActivity this$0;

    ControlsProviderSelectorActivity$onCreate$4(ControlsProviderSelectorActivity controlsProviderSelectorActivity) {
        this.this$0 = controlsProviderSelectorActivity;
    }

    public final void onClick(View view) {
        this.this$0.finishAffinity();
    }
}

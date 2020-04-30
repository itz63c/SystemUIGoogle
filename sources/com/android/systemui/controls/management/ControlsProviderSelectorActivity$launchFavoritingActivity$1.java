package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.Intent;

/* compiled from: ControlsProviderSelectorActivity.kt */
final class ControlsProviderSelectorActivity$launchFavoritingActivity$1 implements Runnable {
    final /* synthetic */ ComponentName $component;
    final /* synthetic */ ControlsProviderSelectorActivity this$0;

    ControlsProviderSelectorActivity$launchFavoritingActivity$1(ControlsProviderSelectorActivity controlsProviderSelectorActivity, ComponentName componentName) {
        this.this$0 = controlsProviderSelectorActivity;
        this.$component = componentName;
    }

    public final void run() {
        ComponentName componentName = this.$component;
        if (componentName != null) {
            Intent intent = new Intent(this.this$0.getApplicationContext(), ControlsFavoritingActivity.class);
            intent.putExtra("extra_app_label", this.this$0.listingController.getAppLabel(componentName));
            intent.putExtra("android.intent.extra.COMPONENT_NAME", componentName);
            intent.setFlags(536870912);
            this.this$0.startActivity(intent);
        }
    }
}

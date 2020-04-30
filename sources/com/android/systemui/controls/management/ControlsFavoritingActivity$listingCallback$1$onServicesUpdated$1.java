package com.android.systemui.controls.management;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$listingCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ ControlsFavoritingActivity$listingCallback$1 this$0;

    ControlsFavoritingActivity$listingCallback$1$onServicesUpdated$1(ControlsFavoritingActivity$listingCallback$1 controlsFavoritingActivity$listingCallback$1) {
        this.this$0 = controlsFavoritingActivity$listingCallback$1;
    }

    public final void run() {
        if (this.this$0.icon != null) {
            ControlsFavoritingActivity.access$getIconView$p(this.this$0.this$0).setImageDrawable(this.this$0.icon);
        }
        ControlsFavoritingActivity.access$getIconFrame$p(this.this$0.this$0).setVisibility(this.this$0.icon != null ? 0 : 8);
    }
}

package com.android.systemui.controls.management;

import android.graphics.drawable.Drawable;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$listingCallback$1 implements ControlsListingCallback {
    /* access modifiers changed from: private */
    public Drawable icon;
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$listingCallback$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public void onServicesUpdated(List<ControlsServiceInfo> list) {
        Drawable drawable;
        Object obj;
        Intrinsics.checkParameterIsNotNull(list, "serviceInfos");
        Iterator it = list.iterator();
        while (true) {
            drawable = null;
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual((Object) ((ControlsServiceInfo) obj).componentName, (Object) this.this$0.component)) {
                break;
            }
        }
        ControlsServiceInfo controlsServiceInfo = (ControlsServiceInfo) obj;
        if (controlsServiceInfo != null) {
            drawable = controlsServiceInfo.loadIcon();
        }
        if (!Intrinsics.areEqual((Object) this.icon, (Object) drawable)) {
            this.icon = drawable;
            this.this$0.executor.execute(new ControlsFavoritingActivity$listingCallback$1$onServicesUpdated$1(this));
        }
    }
}

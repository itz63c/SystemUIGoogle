package com.android.systemui.controls.controller;

import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$listingCallback$1 implements ControlsListingCallback {
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$listingCallback$1(ControlsControllerImpl controlsControllerImpl) {
        this.this$0 = controlsControllerImpl;
    }

    public void onServicesUpdated(List<ControlsServiceInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "serviceInfos");
        this.this$0.executor.execute(new ControlsControllerImpl$listingCallback$1$onServicesUpdated$1(this, list));
    }
}

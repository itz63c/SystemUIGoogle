package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsRequestDialog.kt */
public final class ControlsRequestDialog$callback$1 implements ControlsListingCallback {
    public void onServicesUpdated(List<ControlsServiceInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "serviceInfos");
    }

    ControlsRequestDialog$callback$1() {
    }
}

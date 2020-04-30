package com.android.systemui.controls.management;

import android.content.ComponentName;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.UserAwareController;
import com.android.systemui.statusbar.policy.CallbackController;
import java.util.List;

/* compiled from: ControlsListingController.kt */
public interface ControlsListingController extends CallbackController<ControlsListingCallback>, UserAwareController {

    @FunctionalInterface
    /* compiled from: ControlsListingController.kt */
    public interface ControlsListingCallback {
        void onServicesUpdated(List<ControlsServiceInfo> list);
    }

    CharSequence getAppLabel(ComponentName componentName);
}

package com.android.systemui.controls.p004ui;

import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createCallback$1 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl$createCallback$1 implements ControlsListingCallback {
    final /* synthetic */ Function1 $onResult;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createCallback$1(ControlsUiControllerImpl controlsUiControllerImpl, Function1 function1) {
        this.this$0 = controlsUiControllerImpl;
        this.$onResult = function1;
    }

    public void onServicesUpdated(List<ControlsServiceInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "serviceInfos");
        this.this$0.getBgExecutor().execute(new ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1(this, list));
    }
}

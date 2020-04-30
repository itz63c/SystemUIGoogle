package com.android.systemui.controls.management;

import android.content.pm.ServiceInfo;
import android.util.Log;
import com.android.settingslib.applications.ServiceListing.Callback;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
final class ControlsListingControllerImpl$serviceListingCallback$1 implements Callback {
    final /* synthetic */ ControlsListingControllerImpl this$0;

    ControlsListingControllerImpl$serviceListingCallback$1(ControlsListingControllerImpl controlsListingControllerImpl) {
        this.this$0 = controlsListingControllerImpl;
    }

    public final void onServicesReloaded(List<ServiceInfo> list) {
        Log.d("ControlsListingControllerImpl", "ServiceConfig reloaded");
        ControlsListingControllerImpl controlsListingControllerImpl = this.this$0;
        Intrinsics.checkExpressionValueIsNotNull(list, "it");
        controlsListingControllerImpl.availableServices = CollectionsKt___CollectionsKt.toList(list);
        this.this$0.backgroundExecutor.execute(new Runnable(this) {
            final /* synthetic */ ControlsListingControllerImpl$serviceListingCallback$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                for (ControlsListingCallback onServicesUpdated : this.this$0.this$0.callbacks) {
                    onServicesUpdated.onServicesUpdated(this.this$0.this$0.getCurrentServices());
                }
            }
        });
    }
}

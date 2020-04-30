package com.android.systemui.controls.management;

import android.content.Context;
import com.android.settingslib.applications.ServiceListing;
import com.android.settingslib.applications.ServiceListing.Builder;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
public final class ControlsListingControllerImplKt {
    /* access modifiers changed from: private */
    public static final ServiceListing createServiceListing(Context context) {
        Builder builder = new Builder(context);
        builder.setIntentAction("android.service.controls.ControlsProviderService");
        builder.setPermission("android.permission.BIND_CONTROLS");
        builder.setNoun("Controls Provider");
        String str = "controls_providers";
        builder.setSetting(str);
        builder.setTag(str);
        ServiceListing build = builder.build();
        Intrinsics.checkExpressionValueIsNotNull(build, "ServiceListing.Builder(c…providers\")\n    }.build()");
        return build;
    }
}

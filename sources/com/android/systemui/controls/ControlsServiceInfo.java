package com.android.systemui.controls;

import android.content.Context;
import android.content.pm.ServiceInfo;
import com.android.settingslib.applications.DefaultAppInfo;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsServiceInfo.kt */
public final class ControlsServiceInfo extends DefaultAppInfo {
    public ControlsServiceInfo(Context context, ServiceInfo serviceInfo) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(serviceInfo, "serviceInfo");
        super(context, context.getPackageManager(), context.getUserId(), serviceInfo.getComponentName());
    }
}

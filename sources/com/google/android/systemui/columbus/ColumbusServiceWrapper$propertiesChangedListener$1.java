package com.google.android.systemui.columbus;

import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusServiceWrapper.kt */
final class ColumbusServiceWrapper$propertiesChangedListener$1 implements OnPropertiesChangedListener {
    final /* synthetic */ ColumbusServiceWrapper this$0;

    ColumbusServiceWrapper$propertiesChangedListener$1(ColumbusServiceWrapper columbusServiceWrapper) {
        this.this$0 = columbusServiceWrapper;
    }

    public final void onPropertiesChanged(Properties properties) {
        Intrinsics.checkExpressionValueIsNotNull(properties, "properties");
        String str = "systemui_google_columbus_enabled";
        if (properties.getKeyset().contains(str) && properties.getBoolean(str, false)) {
            this.this$0.startService();
        }
    }
}

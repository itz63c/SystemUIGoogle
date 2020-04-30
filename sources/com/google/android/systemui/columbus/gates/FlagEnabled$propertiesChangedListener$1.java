package com.google.android.systemui.columbus.gates;

import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: FlagEnabled.kt */
final class FlagEnabled$propertiesChangedListener$1 implements OnPropertiesChangedListener {
    final /* synthetic */ FlagEnabled this$0;

    FlagEnabled$propertiesChangedListener$1(FlagEnabled flagEnabled) {
        this.this$0 = flagEnabled;
    }

    public final void onPropertiesChanged(Properties properties) {
        Intrinsics.checkExpressionValueIsNotNull(properties, "properties");
        String str = "systemui_google_columbus_enabled";
        if (properties.getKeyset().contains(str)) {
            this.this$0.columbusEnabled = properties.getBoolean(str, false);
            this.this$0.notifyListener();
        }
    }
}

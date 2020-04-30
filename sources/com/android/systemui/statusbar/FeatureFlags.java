package com.android.systemui.statusbar;

import android.provider.DeviceConfig;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.util.ArrayMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class FeatureFlags {
    private final Map<String, Boolean> mCachedDeviceConfigFlags = new ArrayMap();

    public FeatureFlags(Executor executor) {
        DeviceConfig.addOnPropertiesChangedListener("systemui", executor, new OnPropertiesChangedListener() {
            public final void onPropertiesChanged(Properties properties) {
                FeatureFlags.this.onPropertiesChanged(properties);
            }
        });
    }

    public boolean isNewNotifPipelineEnabled() {
        return getDeviceConfigFlag("notification.newpipeline.enabled", true);
    }

    public boolean isNewNotifPipelineRenderingEnabled() {
        if (!isNewNotifPipelineEnabled() || !getDeviceConfigFlag("notification.newpipeline.rendering", false)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onPropertiesChanged(Properties properties) {
        synchronized (this.mCachedDeviceConfigFlags) {
            for (String remove : properties.getKeyset()) {
                this.mCachedDeviceConfigFlags.remove(remove);
            }
        }
    }

    private boolean getDeviceConfigFlag(String str, boolean z) {
        boolean booleanValue;
        synchronized (this.mCachedDeviceConfigFlags) {
            Boolean bool = (Boolean) this.mCachedDeviceConfigFlags.get(str);
            if (bool == null) {
                bool = Boolean.valueOf(DeviceConfig.getBoolean("systemui", str, z));
                this.mCachedDeviceConfigFlags.put(str, bool);
            }
            booleanValue = bool.booleanValue();
        }
        return booleanValue;
    }
}

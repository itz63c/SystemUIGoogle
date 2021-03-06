package com.android.systemui.assist;

import android.provider.DeviceConfig;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import com.android.systemui.DejankUtils;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class DeviceConfigHelper {
    public long getLong(String str, long j) {
        return ((Long) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(str, j) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ long f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return Long.valueOf(DeviceConfig.getLong("systemui", this.f$0, this.f$1));
            }
        })).longValue();
    }

    public int getInt(String str, int i) {
        return ((Integer) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(str, i) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return Integer.valueOf(DeviceConfig.getInt("systemui", this.f$0, this.f$1));
            }
        })).intValue();
    }

    public String getString(String str, String str2) {
        return (String) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(str, str2) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ String f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return DeviceConfig.getString("systemui", this.f$0, this.f$1);
            }
        });
    }

    public boolean getBoolean(String str, boolean z) {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(str, z) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ boolean f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return Boolean.valueOf(DeviceConfig.getBoolean("systemui", this.f$0, this.f$1));
            }
        })).booleanValue();
    }

    public void addOnPropertiesChangedListener(Executor executor, OnPropertiesChangedListener onPropertiesChangedListener) {
        DeviceConfig.addOnPropertiesChangedListener("systemui", executor, onPropertiesChangedListener);
    }

    public void removeOnPropertiesChangedListener(OnPropertiesChangedListener onPropertiesChangedListener) {
        DeviceConfig.removeOnPropertiesChangedListener(onPropertiesChangedListener);
    }
}

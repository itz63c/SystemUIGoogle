package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.IWallpaperManager;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DozeFactory_Factory implements Factory<DozeFactory> {
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Builder> delayedWakeLockBuilderProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DozeServiceHost> dozeServiceHostProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<ProximitySensor> proximitySensorProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private final Provider<IWallpaperManager> wallpaperManagerProvider;

    public DozeFactory_Factory(Provider<FalsingManager> provider, Provider<DozeLog> provider2, Provider<DozeParameters> provider3, Provider<BatteryController> provider4, Provider<AsyncSensorManager> provider5, Provider<AlarmManager> provider6, Provider<WakefulnessLifecycle> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DockManager> provider9, Provider<IWallpaperManager> provider10, Provider<ProximitySensor> provider11, Provider<Builder> provider12, Provider<Handler> provider13, Provider<BiometricUnlockController> provider14, Provider<BroadcastDispatcher> provider15, Provider<DozeServiceHost> provider16) {
        this.falsingManagerProvider = provider;
        this.dozeLogProvider = provider2;
        this.dozeParametersProvider = provider3;
        this.batteryControllerProvider = provider4;
        this.asyncSensorManagerProvider = provider5;
        this.alarmManagerProvider = provider6;
        this.wakefulnessLifecycleProvider = provider7;
        this.keyguardUpdateMonitorProvider = provider8;
        this.dockManagerProvider = provider9;
        this.wallpaperManagerProvider = provider10;
        this.proximitySensorProvider = provider11;
        this.delayedWakeLockBuilderProvider = provider12;
        this.handlerProvider = provider13;
        this.biometricUnlockControllerProvider = provider14;
        this.broadcastDispatcherProvider = provider15;
        this.dozeServiceHostProvider = provider16;
    }

    public DozeFactory get() {
        return provideInstance(this.falsingManagerProvider, this.dozeLogProvider, this.dozeParametersProvider, this.batteryControllerProvider, this.asyncSensorManagerProvider, this.alarmManagerProvider, this.wakefulnessLifecycleProvider, this.keyguardUpdateMonitorProvider, this.dockManagerProvider, this.wallpaperManagerProvider, this.proximitySensorProvider, this.delayedWakeLockBuilderProvider, this.handlerProvider, this.biometricUnlockControllerProvider, this.broadcastDispatcherProvider, this.dozeServiceHostProvider);
    }

    public static DozeFactory provideInstance(Provider<FalsingManager> provider, Provider<DozeLog> provider2, Provider<DozeParameters> provider3, Provider<BatteryController> provider4, Provider<AsyncSensorManager> provider5, Provider<AlarmManager> provider6, Provider<WakefulnessLifecycle> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DockManager> provider9, Provider<IWallpaperManager> provider10, Provider<ProximitySensor> provider11, Provider<Builder> provider12, Provider<Handler> provider13, Provider<BiometricUnlockController> provider14, Provider<BroadcastDispatcher> provider15, Provider<DozeServiceHost> provider16) {
        DozeFactory dozeFactory = new DozeFactory((FalsingManager) provider.get(), (DozeLog) provider2.get(), (DozeParameters) provider3.get(), (BatteryController) provider4.get(), (AsyncSensorManager) provider5.get(), (AlarmManager) provider6.get(), (WakefulnessLifecycle) provider7.get(), (KeyguardUpdateMonitor) provider8.get(), (DockManager) provider9.get(), (IWallpaperManager) provider10.get(), (ProximitySensor) provider11.get(), (Builder) provider12.get(), (Handler) provider13.get(), (BiometricUnlockController) provider14.get(), (BroadcastDispatcher) provider15.get(), (DozeServiceHost) provider16.get());
        return dozeFactory;
    }

    public static DozeFactory_Factory create(Provider<FalsingManager> provider, Provider<DozeLog> provider2, Provider<DozeParameters> provider3, Provider<BatteryController> provider4, Provider<AsyncSensorManager> provider5, Provider<AlarmManager> provider6, Provider<WakefulnessLifecycle> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DockManager> provider9, Provider<IWallpaperManager> provider10, Provider<ProximitySensor> provider11, Provider<Builder> provider12, Provider<Handler> provider13, Provider<BiometricUnlockController> provider14, Provider<BroadcastDispatcher> provider15, Provider<DozeServiceHost> provider16) {
        DozeFactory_Factory dozeFactory_Factory = new DozeFactory_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16);
        return dozeFactory_Factory;
    }
}

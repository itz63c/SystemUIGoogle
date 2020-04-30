package com.android.systemui.statusbar;

import android.content.Context;
import com.android.internal.app.IBatteryStats;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.wakelock.WakeLock.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardIndicationController_Factory implements Factory<KeyguardIndicationController> {
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<IBatteryStats> iBatteryStatsProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<Builder> wakeLockBuilderProvider;

    public KeyguardIndicationController_Factory(Provider<Context> provider, Provider<Builder> provider2, Provider<KeyguardStateController> provider3, Provider<StatusBarStateController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<DockManager> provider6, Provider<IBatteryStats> provider7) {
        this.contextProvider = provider;
        this.wakeLockBuilderProvider = provider2;
        this.keyguardStateControllerProvider = provider3;
        this.statusBarStateControllerProvider = provider4;
        this.keyguardUpdateMonitorProvider = provider5;
        this.dockManagerProvider = provider6;
        this.iBatteryStatsProvider = provider7;
    }

    public KeyguardIndicationController get() {
        return provideInstance(this.contextProvider, this.wakeLockBuilderProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.keyguardUpdateMonitorProvider, this.dockManagerProvider, this.iBatteryStatsProvider);
    }

    public static KeyguardIndicationController provideInstance(Provider<Context> provider, Provider<Builder> provider2, Provider<KeyguardStateController> provider3, Provider<StatusBarStateController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<DockManager> provider6, Provider<IBatteryStats> provider7) {
        KeyguardIndicationController keyguardIndicationController = new KeyguardIndicationController((Context) provider.get(), (Builder) provider2.get(), (KeyguardStateController) provider3.get(), (StatusBarStateController) provider4.get(), (KeyguardUpdateMonitor) provider5.get(), (DockManager) provider6.get(), (IBatteryStats) provider7.get());
        return keyguardIndicationController;
    }

    public static KeyguardIndicationController_Factory create(Provider<Context> provider, Provider<Builder> provider2, Provider<KeyguardStateController> provider3, Provider<StatusBarStateController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<DockManager> provider6, Provider<IBatteryStats> provider7) {
        KeyguardIndicationController_Factory keyguardIndicationController_Factory = new KeyguardIndicationController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
        return keyguardIndicationController_Factory;
    }
}

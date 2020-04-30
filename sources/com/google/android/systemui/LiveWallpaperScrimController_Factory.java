package com.google.android.systemui;

import android.app.AlarmManager;
import android.app.IWallpaperManager;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LiveWallpaperScrimController_Factory implements Factory<LiveWallpaperScrimController> {
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<Builder> delayedWakeLockBuilderProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LightBarController> lightBarControllerProvider;
    private final Provider<LockscreenWallpaper> lockscreenWallpaperProvider;
    private final Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private final Provider<IWallpaperManager> wallpaperManagerProvider;

    public LiveWallpaperScrimController_Factory(Provider<LightBarController> provider, Provider<DozeParameters> provider2, Provider<AlarmManager> provider3, Provider<KeyguardStateController> provider4, Provider<Builder> provider5, Provider<Handler> provider6, Provider<IWallpaperManager> provider7, Provider<LockscreenWallpaper> provider8, Provider<KeyguardUpdateMonitor> provider9, Provider<SysuiColorExtractor> provider10, Provider<DockManager> provider11, Provider<BlurUtils> provider12) {
        this.lightBarControllerProvider = provider;
        this.dozeParametersProvider = provider2;
        this.alarmManagerProvider = provider3;
        this.keyguardStateControllerProvider = provider4;
        this.delayedWakeLockBuilderProvider = provider5;
        this.handlerProvider = provider6;
        this.wallpaperManagerProvider = provider7;
        this.lockscreenWallpaperProvider = provider8;
        this.keyguardUpdateMonitorProvider = provider9;
        this.sysuiColorExtractorProvider = provider10;
        this.dockManagerProvider = provider11;
        this.blurUtilsProvider = provider12;
    }

    public LiveWallpaperScrimController get() {
        return provideInstance(this.lightBarControllerProvider, this.dozeParametersProvider, this.alarmManagerProvider, this.keyguardStateControllerProvider, this.delayedWakeLockBuilderProvider, this.handlerProvider, this.wallpaperManagerProvider, this.lockscreenWallpaperProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, this.dockManagerProvider, this.blurUtilsProvider);
    }

    public static LiveWallpaperScrimController provideInstance(Provider<LightBarController> provider, Provider<DozeParameters> provider2, Provider<AlarmManager> provider3, Provider<KeyguardStateController> provider4, Provider<Builder> provider5, Provider<Handler> provider6, Provider<IWallpaperManager> provider7, Provider<LockscreenWallpaper> provider8, Provider<KeyguardUpdateMonitor> provider9, Provider<SysuiColorExtractor> provider10, Provider<DockManager> provider11, Provider<BlurUtils> provider12) {
        LiveWallpaperScrimController liveWallpaperScrimController = new LiveWallpaperScrimController((LightBarController) provider.get(), (DozeParameters) provider2.get(), (AlarmManager) provider3.get(), (KeyguardStateController) provider4.get(), (Builder) provider5.get(), (Handler) provider6.get(), (IWallpaperManager) provider7.get(), (LockscreenWallpaper) provider8.get(), (KeyguardUpdateMonitor) provider9.get(), (SysuiColorExtractor) provider10.get(), (DockManager) provider11.get(), (BlurUtils) provider12.get());
        return liveWallpaperScrimController;
    }

    public static LiveWallpaperScrimController_Factory create(Provider<LightBarController> provider, Provider<DozeParameters> provider2, Provider<AlarmManager> provider3, Provider<KeyguardStateController> provider4, Provider<Builder> provider5, Provider<Handler> provider6, Provider<IWallpaperManager> provider7, Provider<LockscreenWallpaper> provider8, Provider<KeyguardUpdateMonitor> provider9, Provider<SysuiColorExtractor> provider10, Provider<DockManager> provider11, Provider<BlurUtils> provider12) {
        LiveWallpaperScrimController_Factory liveWallpaperScrimController_Factory = new LiveWallpaperScrimController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
        return liveWallpaperScrimController_Factory;
    }
}

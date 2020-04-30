package com.android.systemui.statusbar.phone;

import android.app.IWallpaperManager;
import android.app.WallpaperManager;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LockscreenWallpaper_Factory implements Factory<LockscreenWallpaper> {
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<IWallpaperManager> iWallpaperManagerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationMediaManager> mediaManagerProvider;
    private final Provider<WallpaperManager> wallpaperManagerProvider;

    public LockscreenWallpaper_Factory(Provider<WallpaperManager> provider, Provider<IWallpaperManager> provider2, Provider<KeyguardUpdateMonitor> provider3, Provider<DumpManager> provider4, Provider<NotificationMediaManager> provider5, Provider<Handler> provider6) {
        this.wallpaperManagerProvider = provider;
        this.iWallpaperManagerProvider = provider2;
        this.keyguardUpdateMonitorProvider = provider3;
        this.dumpManagerProvider = provider4;
        this.mediaManagerProvider = provider5;
        this.mainHandlerProvider = provider6;
    }

    public LockscreenWallpaper get() {
        return provideInstance(this.wallpaperManagerProvider, this.iWallpaperManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.mediaManagerProvider, this.mainHandlerProvider);
    }

    public static LockscreenWallpaper provideInstance(Provider<WallpaperManager> provider, Provider<IWallpaperManager> provider2, Provider<KeyguardUpdateMonitor> provider3, Provider<DumpManager> provider4, Provider<NotificationMediaManager> provider5, Provider<Handler> provider6) {
        LockscreenWallpaper lockscreenWallpaper = new LockscreenWallpaper((WallpaperManager) provider.get(), (IWallpaperManager) provider2.get(), (KeyguardUpdateMonitor) provider3.get(), (DumpManager) provider4.get(), (NotificationMediaManager) provider5.get(), (Handler) provider6.get());
        return lockscreenWallpaper;
    }

    public static LockscreenWallpaper_Factory create(Provider<WallpaperManager> provider, Provider<IWallpaperManager> provider2, Provider<KeyguardUpdateMonitor> provider3, Provider<DumpManager> provider4, Provider<NotificationMediaManager> provider5, Provider<Handler> provider6) {
        LockscreenWallpaper_Factory lockscreenWallpaper_Factory = new LockscreenWallpaper_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return lockscreenWallpaper_Factory;
    }
}

package com.android.systemui.screenshot;

import android.os.UserManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TakeScreenshotService_Factory implements Factory<TakeScreenshotService> {
    private final Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider;
    private final Provider<GlobalScreenshot> globalScreenshotProvider;
    private final Provider<UserManager> userManagerProvider;

    public TakeScreenshotService_Factory(Provider<GlobalScreenshot> provider, Provider<GlobalScreenshotLegacy> provider2, Provider<UserManager> provider3) {
        this.globalScreenshotProvider = provider;
        this.globalScreenshotLegacyProvider = provider2;
        this.userManagerProvider = provider3;
    }

    public TakeScreenshotService get() {
        return provideInstance(this.globalScreenshotProvider, this.globalScreenshotLegacyProvider, this.userManagerProvider);
    }

    public static TakeScreenshotService provideInstance(Provider<GlobalScreenshot> provider, Provider<GlobalScreenshotLegacy> provider2, Provider<UserManager> provider3) {
        return new TakeScreenshotService((GlobalScreenshot) provider.get(), (GlobalScreenshotLegacy) provider2.get(), (UserManager) provider3.get());
    }

    public static TakeScreenshotService_Factory create(Provider<GlobalScreenshot> provider, Provider<GlobalScreenshotLegacy> provider2, Provider<UserManager> provider3) {
        return new TakeScreenshotService_Factory(provider, provider2, provider3);
    }
}

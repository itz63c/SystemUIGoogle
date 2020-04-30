package com.android.systemui.screenshot;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GlobalScreenshot_Factory implements Factory<GlobalScreenshot> {
    private final Provider<Context> contextProvider;
    private final Provider<LayoutInflater> layoutInflaterProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<ScreenshotNotificationsController> screenshotNotificationsControllerProvider;

    public GlobalScreenshot_Factory(Provider<Context> provider, Provider<Resources> provider2, Provider<LayoutInflater> provider3, Provider<ScreenshotNotificationsController> provider4) {
        this.contextProvider = provider;
        this.resourcesProvider = provider2;
        this.layoutInflaterProvider = provider3;
        this.screenshotNotificationsControllerProvider = provider4;
    }

    public GlobalScreenshot get() {
        return provideInstance(this.contextProvider, this.resourcesProvider, this.layoutInflaterProvider, this.screenshotNotificationsControllerProvider);
    }

    public static GlobalScreenshot provideInstance(Provider<Context> provider, Provider<Resources> provider2, Provider<LayoutInflater> provider3, Provider<ScreenshotNotificationsController> provider4) {
        return new GlobalScreenshot((Context) provider.get(), (Resources) provider2.get(), (LayoutInflater) provider3.get(), (ScreenshotNotificationsController) provider4.get());
    }

    public static GlobalScreenshot_Factory create(Provider<Context> provider, Provider<Resources> provider2, Provider<LayoutInflater> provider3, Provider<ScreenshotNotificationsController> provider4) {
        return new GlobalScreenshot_Factory(provider, provider2, provider3, provider4);
    }
}

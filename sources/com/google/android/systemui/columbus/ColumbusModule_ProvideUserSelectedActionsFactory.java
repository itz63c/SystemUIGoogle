package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.LaunchCamera;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Map;
import javax.inject.Provider;

public final class ColumbusModule_ProvideUserSelectedActionsFactory implements Factory<Map<String, Action>> {
    private final Provider<LaunchCamera> launchCameraProvider;
    private final Provider<LaunchOpa> launchOpaProvider;
    private final Provider<LaunchOverview> launchOverviewProvider;
    private final Provider<ManageMedia> manageMediaProvider;
    private final Provider<TakeScreenshot> takeScreenshotProvider;

    public ColumbusModule_ProvideUserSelectedActionsFactory(Provider<LaunchOpa> provider, Provider<LaunchCamera> provider2, Provider<ManageMedia> provider3, Provider<TakeScreenshot> provider4, Provider<LaunchOverview> provider5) {
        this.launchOpaProvider = provider;
        this.launchCameraProvider = provider2;
        this.manageMediaProvider = provider3;
        this.takeScreenshotProvider = provider4;
        this.launchOverviewProvider = provider5;
    }

    public Map<String, Action> get() {
        return provideInstance(this.launchOpaProvider, this.launchCameraProvider, this.manageMediaProvider, this.takeScreenshotProvider, this.launchOverviewProvider);
    }

    public static Map<String, Action> provideInstance(Provider<LaunchOpa> provider, Provider<LaunchCamera> provider2, Provider<ManageMedia> provider3, Provider<TakeScreenshot> provider4, Provider<LaunchOverview> provider5) {
        return proxyProvideUserSelectedActions((LaunchOpa) provider.get(), (LaunchCamera) provider2.get(), (ManageMedia) provider3.get(), (TakeScreenshot) provider4.get(), (LaunchOverview) provider5.get());
    }

    public static ColumbusModule_ProvideUserSelectedActionsFactory create(Provider<LaunchOpa> provider, Provider<LaunchCamera> provider2, Provider<ManageMedia> provider3, Provider<TakeScreenshot> provider4, Provider<LaunchOverview> provider5) {
        ColumbusModule_ProvideUserSelectedActionsFactory columbusModule_ProvideUserSelectedActionsFactory = new ColumbusModule_ProvideUserSelectedActionsFactory(provider, provider2, provider3, provider4, provider5);
        return columbusModule_ProvideUserSelectedActionsFactory;
    }

    public static Map<String, Action> proxyProvideUserSelectedActions(LaunchOpa launchOpa, LaunchCamera launchCamera, ManageMedia manageMedia, TakeScreenshot takeScreenshot, LaunchOverview launchOverview) {
        Map<String, Action> provideUserSelectedActions = ColumbusModule.provideUserSelectedActions(launchOpa, launchCamera, manageMedia, takeScreenshot, launchOverview);
        Preconditions.checkNotNull(provideUserSelectedActions, "Cannot return null from a non-@Nullable @Provides method");
        return provideUserSelectedActions;
    }
}

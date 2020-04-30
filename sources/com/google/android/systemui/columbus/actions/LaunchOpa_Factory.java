package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.tuner.TunerService;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class LaunchOpa_Factory implements Factory<LaunchOpa> {
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Set<FeedbackEffect>> feedbackEffectsProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public LaunchOpa_Factory(Provider<Context> provider, Provider<StatusBar> provider2, Provider<Set<FeedbackEffect>> provider3, Provider<AssistManager> provider4, Provider<TunerService> provider5, Provider<ColumbusContentObserver.Factory> provider6) {
        this.contextProvider = provider;
        this.statusBarProvider = provider2;
        this.feedbackEffectsProvider = provider3;
        this.assistManagerProvider = provider4;
        this.tunerServiceProvider = provider5;
        this.settingsObserverFactoryProvider = provider6;
    }

    public LaunchOpa get() {
        return provideInstance(this.contextProvider, this.statusBarProvider, this.feedbackEffectsProvider, this.assistManagerProvider, this.tunerServiceProvider, this.settingsObserverFactoryProvider);
    }

    public static LaunchOpa provideInstance(Provider<Context> provider, Provider<StatusBar> provider2, Provider<Set<FeedbackEffect>> provider3, Provider<AssistManager> provider4, Provider<TunerService> provider5, Provider<ColumbusContentObserver.Factory> provider6) {
        LaunchOpa launchOpa = new LaunchOpa((Context) provider.get(), (StatusBar) provider2.get(), (Set) provider3.get(), (AssistManager) provider4.get(), (TunerService) provider5.get(), (ColumbusContentObserver.Factory) provider6.get());
        return launchOpa;
    }

    public static LaunchOpa_Factory create(Provider<Context> provider, Provider<StatusBar> provider2, Provider<Set<FeedbackEffect>> provider3, Provider<AssistManager> provider4, Provider<TunerService> provider5, Provider<ColumbusContentObserver.Factory> provider6) {
        LaunchOpa_Factory launchOpa_Factory = new LaunchOpa_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return launchOpa_Factory;
    }
}

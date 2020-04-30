package com.google.android.systemui.assist.uihints;

import android.content.Context;
import android.view.ViewGroup;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GlowController_Factory implements Factory<GlowController> {
    private final Provider<Context> contextProvider;
    private final Provider<ViewGroup> parentProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;

    public GlowController_Factory(Provider<Context> provider, Provider<ViewGroup> provider2, Provider<TouchInsideHandler> provider3) {
        this.contextProvider = provider;
        this.parentProvider = provider2;
        this.touchInsideHandlerProvider = provider3;
    }

    public GlowController get() {
        return provideInstance(this.contextProvider, this.parentProvider, this.touchInsideHandlerProvider);
    }

    public static GlowController provideInstance(Provider<Context> provider, Provider<ViewGroup> provider2, Provider<TouchInsideHandler> provider3) {
        return new GlowController((Context) provider.get(), (ViewGroup) provider2.get(), (TouchInsideHandler) provider3.get());
    }

    public static GlowController_Factory create(Provider<Context> provider, Provider<ViewGroup> provider2, Provider<TouchInsideHandler> provider3) {
        return new GlowController_Factory(provider, provider2, provider3);
    }
}

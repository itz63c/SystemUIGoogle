package com.google.android.systemui.assist.uihints.edgelights;

import android.content.Context;
import android.view.ViewGroup;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class EdgeLightsController_Factory implements Factory<EdgeLightsController> {
    private final Provider<Context> contextProvider;
    private final Provider<ViewGroup> parentProvider;

    public EdgeLightsController_Factory(Provider<Context> provider, Provider<ViewGroup> provider2) {
        this.contextProvider = provider;
        this.parentProvider = provider2;
    }

    public EdgeLightsController get() {
        return provideInstance(this.contextProvider, this.parentProvider);
    }

    public static EdgeLightsController provideInstance(Provider<Context> provider, Provider<ViewGroup> provider2) {
        return new EdgeLightsController((Context) provider.get(), (ViewGroup) provider2.get());
    }

    public static EdgeLightsController_Factory create(Provider<Context> provider, Provider<ViewGroup> provider2) {
        return new EdgeLightsController_Factory(provider, provider2);
    }
}

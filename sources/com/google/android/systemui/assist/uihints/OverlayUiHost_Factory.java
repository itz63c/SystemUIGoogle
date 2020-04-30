package com.google.android.systemui.assist.uihints;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class OverlayUiHost_Factory implements Factory<OverlayUiHost> {
    private final Provider<Context> contextProvider;
    private final Provider<TouchOutsideHandler> touchOutsideProvider;

    public OverlayUiHost_Factory(Provider<Context> provider, Provider<TouchOutsideHandler> provider2) {
        this.contextProvider = provider;
        this.touchOutsideProvider = provider2;
    }

    public OverlayUiHost get() {
        return provideInstance(this.contextProvider, this.touchOutsideProvider);
    }

    public static OverlayUiHost provideInstance(Provider<Context> provider, Provider<TouchOutsideHandler> provider2) {
        return new OverlayUiHost((Context) provider.get(), (TouchOutsideHandler) provider2.get());
    }

    public static OverlayUiHost_Factory create(Provider<Context> provider, Provider<TouchOutsideHandler> provider2) {
        return new OverlayUiHost_Factory(provider, provider2);
    }
}

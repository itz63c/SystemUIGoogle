package com.google.android.systemui.columbus.feedback;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class HapticClick_Factory implements Factory<HapticClick> {
    private final Provider<Context> contextProvider;

    public HapticClick_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public HapticClick get() {
        return provideInstance(this.contextProvider);
    }

    public static HapticClick provideInstance(Provider<Context> provider) {
        return new HapticClick((Context) provider.get());
    }

    public static HapticClick_Factory create(Provider<Context> provider) {
        return new HapticClick_Factory(provider);
    }
}

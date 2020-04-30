package com.google.android.systemui.assist.uihints;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ColorChangeHandler_Factory implements Factory<ColorChangeHandler> {
    private final Provider<Context> contextProvider;

    public ColorChangeHandler_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public ColorChangeHandler get() {
        return provideInstance(this.contextProvider);
    }

    public static ColorChangeHandler provideInstance(Provider<Context> provider) {
        return new ColorChangeHandler((Context) provider.get());
    }

    public static ColorChangeHandler_Factory create(Provider<Context> provider) {
        return new ColorChangeHandler_Factory(provider);
    }
}

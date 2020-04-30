package com.google.android.systemui.assist.uihints;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TakeScreenshotHandler_Factory implements Factory<TakeScreenshotHandler> {
    private final Provider<Context> contextProvider;

    public TakeScreenshotHandler_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public TakeScreenshotHandler get() {
        return provideInstance(this.contextProvider);
    }

    public static TakeScreenshotHandler provideInstance(Provider<Context> provider) {
        return new TakeScreenshotHandler((Context) provider.get());
    }

    public static TakeScreenshotHandler_Factory create(Provider<Context> provider) {
        return new TakeScreenshotHandler_Factory(provider);
    }
}

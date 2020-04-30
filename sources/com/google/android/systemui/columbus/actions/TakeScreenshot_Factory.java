package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TakeScreenshot_Factory implements Factory<TakeScreenshot> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;

    public TakeScreenshot_Factory(Provider<Context> provider, Provider<Handler> provider2) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
    }

    public TakeScreenshot get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }

    public static TakeScreenshot provideInstance(Provider<Context> provider, Provider<Handler> provider2) {
        return new TakeScreenshot((Context) provider.get(), (Handler) provider2.get());
    }

    public static TakeScreenshot_Factory create(Provider<Context> provider, Provider<Handler> provider2) {
        return new TakeScreenshot_Factory(provider, provider2);
    }
}

package com.android.systemui.dagger;

import android.os.Handler;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class DependencyProvider_ProvideHandlerFactory implements Factory<Handler> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideHandlerFactory(DependencyProvider dependencyProvider) {
        this.module = dependencyProvider;
    }

    public Handler get() {
        return provideInstance(this.module);
    }

    public static Handler provideInstance(DependencyProvider dependencyProvider) {
        return proxyProvideHandler(dependencyProvider);
    }

    public static DependencyProvider_ProvideHandlerFactory create(DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideHandlerFactory(dependencyProvider);
    }

    public static Handler proxyProvideHandler(DependencyProvider dependencyProvider) {
        Handler provideHandler = dependencyProvider.provideHandler();
        Preconditions.checkNotNull(provideHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideHandler;
    }
}

package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ManageMedia_Factory implements Factory<ManageMedia> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;

    public ManageMedia_Factory(Provider<Context> provider, Provider<Handler> provider2) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
    }

    public ManageMedia get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }

    public static ManageMedia provideInstance(Provider<Context> provider, Provider<Handler> provider2) {
        return new ManageMedia((Context) provider.get(), (Handler) provider2.get());
    }

    public static ManageMedia_Factory create(Provider<Context> provider, Provider<Handler> provider2) {
        return new ManageMedia_Factory(provider, provider2);
    }
}

package com.google.android.systemui.columbus;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ContentResolverWrapper_Factory implements Factory<ContentResolverWrapper> {
    private final Provider<Context> contextProvider;

    public ContentResolverWrapper_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public ContentResolverWrapper get() {
        return provideInstance(this.contextProvider);
    }

    public static ContentResolverWrapper provideInstance(Provider<Context> provider) {
        return new ContentResolverWrapper((Context) provider.get());
    }

    public static ContentResolverWrapper_Factory create(Provider<Context> provider) {
        return new ContentResolverWrapper_Factory(provider);
    }
}

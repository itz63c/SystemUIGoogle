package com.google.android.systemui.assist.uihints;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistantWarmer_Factory implements Factory<AssistantWarmer> {
    private final Provider<Context> contextProvider;

    public AssistantWarmer_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public AssistantWarmer get() {
        return provideInstance(this.contextProvider);
    }

    public static AssistantWarmer provideInstance(Provider<Context> provider) {
        return new AssistantWarmer((Context) provider.get());
    }

    public static AssistantWarmer_Factory create(Provider<Context> provider) {
        return new AssistantWarmer_Factory(provider);
    }
}

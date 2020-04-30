package com.android.systemui;

import android.content.Context;
import com.android.systemui.SystemUIFactory.ContextHolder;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SystemUIFactory_ContextHolder_ProvideContextFactory implements Factory<Context> {
    private final ContextHolder module;

    public SystemUIFactory_ContextHolder_ProvideContextFactory(ContextHolder contextHolder) {
        this.module = contextHolder;
    }

    public Context get() {
        return provideInstance(this.module);
    }

    public static Context provideInstance(ContextHolder contextHolder) {
        return proxyProvideContext(contextHolder);
    }

    public static SystemUIFactory_ContextHolder_ProvideContextFactory create(ContextHolder contextHolder) {
        return new SystemUIFactory_ContextHolder_ProvideContextFactory(contextHolder);
    }

    public static Context proxyProvideContext(ContextHolder contextHolder) {
        Context provideContext = contextHolder.provideContext();
        Preconditions.checkNotNull(provideContext, "Cannot return null from a non-@Nullable @Provides method");
        return provideContext;
    }
}

package com.google.android.systemui.assist;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class OpaEnabledDispatcher_Factory implements Factory<OpaEnabledDispatcher> {
    private final Provider<StatusBar> statusBarLazyProvider;

    public OpaEnabledDispatcher_Factory(Provider<StatusBar> provider) {
        this.statusBarLazyProvider = provider;
    }

    public OpaEnabledDispatcher get() {
        return provideInstance(this.statusBarLazyProvider);
    }

    public static OpaEnabledDispatcher provideInstance(Provider<StatusBar> provider) {
        return new OpaEnabledDispatcher(DoubleCheck.lazy(provider));
    }

    public static OpaEnabledDispatcher_Factory create(Provider<StatusBar> provider) {
        return new OpaEnabledDispatcher_Factory(provider);
    }
}
